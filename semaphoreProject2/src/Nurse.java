import java.util.concurrent.Semaphore;

public class Nurse implements Runnable {
    public static long time = System.currentTimeMillis();
    public static Semaphore nurseIsDone;
    private String threadName;
    public Nurse(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
        nurseIsDone = new Semaphore(0,true);
    }

    @Override
    public void run() {
        sleep(3000);//simulate driving to school
        msg("arrived to the school");
        waitForPrincipalToFinishDeciding(); //waits for principal to signal if Nurse can conduct tests
        msg("arrived at her office to administer tests");
        callStudentsIntoOffice(); //conduct covid tests

        nurseIsDone.release(); //tell teacher nurse is done
        nurseIsDone.release();
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            msg("interrupted");
        }
    }
    /** wait to be signaled by the principal to start covid tests**/
    private void waitForPrincipalToFinishDeciding(){
        try {
            Main.allStudentsHaveDestination.acquire();
        } catch (InterruptedException e) {
            msg("all students have destination interrupted");
        }
    }
    /** Until the nursesRoomQueue isn't empty, call the conductCovidTest() method. Then signal students to leave the nurses room with the
     * nursesRoom semaphore **/
    private void callStudentsIntoOffice(){
        while(!Main.nursesRoomQueue.isEmpty()){
            if(Main.nursesRoomQueue.peek() != null) {
                msg("giving a covid test to student " + Main.nursesRoomQueue.peek());
                conductCovidTest(Main.nursesRoomQueue.poll());
                Main.nursesRoom.release();
            }

        }
    }

    /** conducts covid test on each student by checking the probability. If a random number between 1 to 100 is 3 or less
     * then the student has covid. otherwise they don't **/
    private void conductCovidTest(int student){
        if ((int) (Math.random() * (100) + 1) <= 3) {
            Main.hasCovid[student] = true;
            msg("student " + student + " tested positive for COVID and is being sent home");
        } else {
            msg("Student " + student + " tested negative and is being sent to class");
            Main.classRoomQueue.add(student);
        }
    }
    private void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+threadName+": "+m);
    }
}
