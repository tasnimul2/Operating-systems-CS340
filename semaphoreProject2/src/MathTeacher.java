import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class MathTeacher implements Runnable{
    public static long time = System.currentTimeMillis();
    private String threadName;
    public static volatile Queue<Integer> mathClassSession;
    public static volatile Semaphore availableMathSeats;


    public MathTeacher(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
        mathClassSession = new LinkedList<>();
        availableMathSeats = new Semaphore(6);
    }

    /** Run method: check if the nurse is done with the students. When the nurse is done signal all students that the teacher is here
     * then start class and wait for principal to tell teacher session is over. Then wait for principal to finish doing attendance.
     * once done, go home**/
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

    /** wait for nurse to signal that the nurse is done**/
    private void waitForNurseToFinish(){
        try {
            Nurse.nurseIsDone.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /** simulate walking to class with sleep**/
    private void walkToClass(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            msg("interrupted");
        }
    }

    /** while waiting for the principal to signal the end of class, the class is in session.
     * when the principal signals the end of class, all the the students who has acquired a
     * seat is told to leave the seat to free up room for the next session**/
    private void goToClassSession(){
        try {
            Principal.endClassSignal.acquire();
            while (availableMathSeats.getQueueLength() > 0) {
                mathClassSession.poll();
                availableMathSeats.release();
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+threadName+": "+m);
    }
}
