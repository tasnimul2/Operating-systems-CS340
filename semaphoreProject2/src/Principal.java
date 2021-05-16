import java.util.concurrent.Semaphore;

public class Principal implements Runnable{
    public static long time = System.currentTimeMillis();
    private String threadName;
    public static Semaphore endClassSignal;

    public Principal(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
        endClassSignal = new Semaphore(0);
    }

    @Override
    public void run() {
        msg("is on his way to let students in...");
        letStudentsIn();
        sleep(1000);
        makeDecisionForEachStudent();
        Main.allStudentsHaveDestination.release();
        startNewClassSession();
        sleep(6000);
        msg("waited 6 sec");

    }
    private void letStudentsIn(){
        for(int i = 0; i < Main.yard.length;i++){
            if(Main.yard[i] == 1) {
                Main.yard[i] = 2;
                msg("letting in student " + i + " into school");
            }
            Main.enterSchool.release();
        }
    }
    private void startNewClassSession(){
        sleep(10000);//class sessions in progress
        msg("Class Sessions ending");
        endClassSignal.release();
        endClassSignal.release();
    }

    private void makeDecisionForEachStudent(){
        for(int i = 0; i < Main.yard.length;i++){
            if(Main.yard[i] == 2) {
                sendToNurseOrClass(i);
                Main.waitForPrincipalDecision.release();
            }
        }
    }

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
