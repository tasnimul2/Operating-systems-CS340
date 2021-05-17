import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Principal implements Runnable{
    public static long time = System.currentTimeMillis();
    private String threadName;
    public static Semaphore endClassSignal;
    public static Semaphore doAttendance;
    public static AtomicBoolean studentsNotDoneWithClass;

    public Principal(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
        endClassSignal = new Semaphore(0);
        doAttendance = new Semaphore(0);
    }

    /** Run Method: Initially lets the student into the school. sleeps for 1 second to simulate moving to a different location
     * to tell students where to go. Once making decision, principal signals nurse he is done with allStudentsHaveDestination semaphore.
     * so that the nurse can start testing all students who were sent to be tested. Then the principal simulates classes in session
     * with the startNewClassSession() method and signals the teacher when done with the endClassSignal semaphore Then the principal takes
     * the attendance of the students and when finished, signals the teachers that they can lave by signaling doAttendance.**/

    @Override
    public void run() {
        msg("is on his way to let students in...");
        letStudentsIn();
        sleep(1000);
        makeDecisionForEachStudent();
        Main.allStudentsHaveDestination.release();
        startNewClassSession();

        sleep(10000);
        while(Main.getAttendenceTaken.getQueueLength() > 0){
            Main.getAttendenceTaken.release();
        }
        sleep(3000);
        doAttendance.release();
        doAttendance.release();
        sleep(3000);
        msg("SCHOOL DAY OVER. PRINCIPAL IS NOW LEAVING");


    }
    /** loops the the school yard and if there is a student there ie. yard[i] is 1, then turn it to 2, signifying that the
     * student was not late.**/
    private void letStudentsIn(){
        for(int i = 0; i < Main.yard.length;i++){
            if(Main.yard[i] == 1) {
                Main.yard[i] = 2;
                msg("letting in student " + i + " into school");
            }
            Main.enterSchool.release();
        }
    }


    /** decide whether the student should do to class of curse by calling on the sendToNurseOrClass() method**/
    private void makeDecisionForEachStudent(){
        for(int i = 0; i < Main.yard.length;i++){
            if(Main.yard[i] == 2) {
                sendToNurseOrClass(i);
                Main.waitForPrincipalDecision.release();
            }
        }
    }

    /** out of every 3 students, pick a student to do to the nurse. set the goToNurse flag for that student to be true that way
     * it will keep track of tested students for the future. Add that student to the nursesRoom queue. or else add that student
     * to the classRoomQueue**/

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
    /** simulates the classing running with 10 second sleep then signals the teachers that the class is over**/
    private void startNewClassSession(){
        sleep(10000);//class sessions in progress
        msg("Class Sessions ending");
        endClassSignal.release();
        endClassSignal.release();
    }


    private void sleep(int time){
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
