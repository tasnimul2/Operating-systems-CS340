import java.util.concurrent.Semaphore;

public class ElaTeacher implements Runnable{
    public static long time = System.currentTimeMillis();
    private String threadName;
    public static Semaphore elaClassSession;

    public ElaTeacher(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
        elaClassSession = new Semaphore(6,true);
    }

    @Override
    public void run() {
        waitForNurseToFinish();
        walkToClass(3000);
        msg("Ready to start class...");
        while (Main.numStudentsWaiting.getAndDecrement() > 0){
            Main.waitForTeacherToArrive.release();
        }
    }

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

    private void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+threadName+": "+m);
    }
}
