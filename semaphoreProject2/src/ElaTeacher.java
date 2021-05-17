import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ElaTeacher implements Runnable{
    public static long time = System.currentTimeMillis();
    private String threadName;
    public static volatile Queue<Integer> elaClassSession;
    public static volatile Semaphore availableElaSeats;

    public ElaTeacher(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
        elaClassSession = new LinkedList<>();
        availableElaSeats = new Semaphore(6);
    }

    @Override
    public void run() {
        waitForNurseToFinish();
        walkToClass(3000);
        msg("Ready to start class...");
        while (Main.numStudentsWaiting.getAndDecrement() > 0){
            Main.waitForTeacherToArrive.release();
        }
        goToClassSession();
        try {
            Principal.doAttendance.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msg("DONE TEACHING. HEADED HOME");


    }//end of run

    private void waitForNurseToFinish(){
        try {
            Nurse.nurseIsDone.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void walkToClass(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            msg("interrupted");
        }
    }

    private void goToClassSession(){
        try {
            Principal.endClassSignal.acquire();
            while (availableElaSeats.getQueueLength() > 0) {
                elaClassSession.poll();
                availableElaSeats.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+threadName+": "+m);
    }
}
