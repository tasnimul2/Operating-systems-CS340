import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Student implements  Runnable{
    public volatile int studentID;
    public static long time = System.currentTimeMillis();



    public Student(int id){
        this.studentID = id;


    }

    @Override
    public void run() {
        goToYard();
        try {
            Main.enterSchool.acquire();
            msg("is now in the school and waiting for principals decision");
            if(wasStudentLate()){
                msg("was late to school and is being sent home");
                return;
            }
            Main.waitForPrincipalDecision.acquire();
            if(Main.goToNurse[studentID]){
                msg("waiting for nurse to arrive");
                Main.nursesRoom.acquire();
                if(Main.hasCovid[studentID]){
                    msg("Tested positive for COVID and is now going home");
                    return;
                }
                msg("left the nurses room");
            }
            msg("is headed to class");
            msg("Waiting for teacher to arrive");
            Main.numStudentsWaiting.incrementAndGet();
            Main.waitForTeacherToArrive.acquire();
            findAClass();
            sleep(6000);
            msg("waited 6 sec");

        } catch (InterruptedException e) {
            System.out.println("Student " + studentID + " is interrupted");
        }

    }

    private boolean wasStudentLate(){
        return Main.yard[studentID] == 1;
    }
    private void goToYard(){
        msg("On the way to the school yard");
        Main.yard[studentID] = 1;
    }
    private void findAClass(){
        try {
            if(!Main.wentToELA[studentID] && ElaTeacher.elaClassSession.size() < 6){
                ElaTeacher.elaClassSession.add(studentID);
                ElaTeacher.availableElaSeats.acquire();
                Main.wentToELA[studentID] = true;
                msg("went ELA Class");
            }else if(!Main.wentToMath[studentID] && MathTeacher.mathClassSession.size() < 6){
                MathTeacher.mathClassSession.add(studentID);
                MathTeacher.availableMathSeats.acquire();
                Main.wentToMath[studentID] = true;
                msg("went Math Class");
            }else {
                msg("playing in the yard");
                sleep(3000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        System.out.println("["+(System.currentTimeMillis()-time)+"] Student# "+studentID+": "+m);
    }
}
