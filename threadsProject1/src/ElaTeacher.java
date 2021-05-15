import java.util.LinkedList;
import java.util.Queue;

public class ElaTeacher implements Runnable{
    public static long time = System.currentTimeMillis();
    public static volatile Queue<Integer> classAllocationQueue;
    private String threadName;
    public ElaTeacher(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
        classAllocationQueue = new LinkedList<>();
    }
    @Override
    public void run() {
        try{
            Thread.sleep(20000);
            Main.principal.interrupt();
            msg("classroom waiting line: " + Main.classRoomQueue.size());
            msg("UNLOCKING THE DOOR");
            msg("STARTING ELA CLASS");
            Thread.currentThread().setPriority(1);
            /** go through all the students queued to attend classes.Take their attendance  Join the student thread to continue the next processes**/
            while (!Main.classRoomQueue.isEmpty()){
                Thread.sleep(100);//to allow different threads to alternate.
                if(Main.classRoomQueue.peek() !=null) {
                    msg("is allowing student #" + Main.classRoomQueue.peek()+ " to enter the ELA class");
                    classAllocationQueue.add(Main.classRoomQueue.peek());
                    Main.attendance.add(Main.classRoomQueue.peek());
                    Main.students[Main.classRoomQueue.peek()].interrupt();
                    Main.students[Main.classRoomQueue.poll()].join();
                }
            }
            /** then the teacher breaks out of join**/
            Thread.sleep(5000);
            msg("Done Teaching");
        }catch (InterruptedException e){
            System.out.println("Teacher Interrupted");
        }
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
