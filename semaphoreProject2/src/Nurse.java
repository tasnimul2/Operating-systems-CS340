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
        waitForPrincipalToFinishDeciding();
        msg("arrived at her office to administer tests");
        callStudentsIntoOffice();

        nurseIsDone.release();
        nurseIsDone.release();
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            msg("interrupted");
        }
    }
    private void waitForPrincipalToFinishDeciding(){
        try {
            Main.allStudentsHaveDestination.acquire();
        } catch (InterruptedException e) {
            msg("all students have destination interrupted");
        }
    }
    private void callStudentsIntoOffice(){
        while(!Main.nursesRoomQueue.isEmpty()){
            if(Main.nursesRoomQueue.peek() != null) {
                msg("giving a covid test to student " + Main.nursesRoomQueue.peek());
                conductCovidTest(Main.nursesRoomQueue.poll());
                Main.nursesRoom.release();
            }

        }
    }

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
