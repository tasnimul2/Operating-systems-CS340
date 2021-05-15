public class Nurse implements Runnable{
    public static long time = System.currentTimeMillis();
    private String threadName;
    public Nurse(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
    }
    @Override
    public void run() {
        try{
            /** the nurse checks which students are in the nurses room's queue (the students were put in this queue by the principal)
             * then the nurse interrupts these students to continue the following processes in the student thread**/
            Thread.sleep(10000);
            System.out.println("Nurses waiting Room Size:" + Main.nursesRoomQueue.size());
            while(!Main.nursesRoomQueue.isEmpty()){
                if(Main.nursesRoomQueue.peek() != null) {
                    Main.students[Main.nursesRoomQueue.poll()].interrupt();
                    msg("is about to give a shot to student ");
                }
            }
            checkHowManySick();
        }catch (InterruptedException e){
            System.out.println("The nurse is interrupted");
        }
        msg("DONE TESTING AND VACCINATING STUDENTS");
    }

    private void checkHowManySick(){
        if(Main.numSick.get() > Main.maxSick){
            return;
        }
    }

    private void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+threadName+": "+m);
    }
}
