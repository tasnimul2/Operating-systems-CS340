import java.util.TreeSet;

public class Principal implements Runnable{
    public static long time = System.currentTimeMillis();

    private String threadName;
    public Principal(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
    }
    @Override
    public void run() {
        try{

            Thread.sleep(3000);
            msg("arrived to check students questionnaire");
            /** go through all the students in the yard. If value == 1, then there is a student at that index.**/
            for(int student = 0; student < Main.yard.length; student++){
                if(Main.yard[student] == 1){
                    if(!Main.questionnaire[student]){
                        Main.sendHome[student] = true;
                    }else {
                        /**decides whether to send student to to the nurse or to class**/
                        sendToNurseOrClass(student);
                    }
                    Main.students[student].interrupt();

                }
            }
            Main.teacher1.join();
            msg("Principal joined");

        }catch (InterruptedException e){
            msg(" has to now teach Physical Education");
            try{
                msg("classroom waiting line: " + Main.classRoomQueue.size());
                msg("UNLOCKING THE DOOR");
                msg("STARTING PhysEd CLASS");
                Thread.currentThread().setPriority(1);
                while (!Main.classRoomQueue.isEmpty()){
                    Thread.sleep(100);//to allow different threads to alternate.
                    if(Main.classRoomQueue.peek() !=null) {
                        msg("is allowing student #" + Main.classRoomQueue.peek()+ " to enter the Yard for PhysEd");
                        Main.attendance.add(Main.classRoomQueue.peek());
                        Main.students[Main.classRoomQueue.peek()].interrupt();
                        Main.students[Main.classRoomQueue.poll()].join();
                    }
                }


                Main.teacher2.join();
                msg("Done Teaching");
                int i = 0;
                String firstClass;
                String secondClass;
                String thirdClass;
                while(!Main.attendance.isEmpty()){
                    int num = (int)(Math.random()*(4)+1);
                    if(num ==4){
                        firstClass = "ELA -->";
                        secondClass = "PhysEd -->";
                        thirdClass = "Math ";
                    }else if(num ==3){
                        firstClass = "Math -->";
                        secondClass = "PhysEd -->";
                        thirdClass = "ELA";
                    }else if(num ==2){
                        firstClass = "PhysEd -->";
                        secondClass = "ELA -->";
                        thirdClass = "Math ";
                    }else{
                        firstClass = "PhysEd -->";
                        secondClass = "Math -->";
                        thirdClass = "ELA ";
                    }

                    System.out.println("["+(System.currentTimeMillis()-time)+"] Student "+ Main.attendance.pollFirst() + ": attended "+firstClass+secondClass+thirdClass + " -->leaving");
                    i++;
                }

            }catch (InterruptedException ie){
                System.out.println("Principal Interrupted");
            }
        }

    }

    /** if the random number generated  is 1 out of 3, then there is a 3% chance of it happening. Hence, send that student to the nurse
     * otherwise send them to class **/
    private void sendToNurseOrClass(int student){
        if ((int) (Math.random() * (3) + 1) == 1) {
            Main.goToNurse[student] = true;
            Main.nursesRoomQueue.add(student);
            msg("tells student #" + student + " to see the nurse");
        } else {
            Main.goToNurse[student] = false;
            Main.classRoomQueue.add(student);
            msg("tells student #"+student+ " to go to class");
        }
    }
    private void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+threadName+": "+m);
    }
}
