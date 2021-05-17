import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Student implements  Runnable{
    public volatile int studentID;
    public static long time = System.currentTimeMillis();
    public static Semaphore mutex1;
    public static AtomicInteger currEla,currMath;
    private Queue<String> studentAttendance ;



    public Student(int id){
        this.studentID = id;
        mutex1 = new Semaphore(1);
        currEla = new AtomicInteger(0);
        currMath = new AtomicInteger(0);
        studentAttendance = new LinkedList<>();
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
            sleep(10000);
            msg("is going to second session");
            goToOtherClasses();
            Main.getAttendenceTaken.acquire();
            //msg("attendance");
            lineUpStudentsInOrder();
            provideAttendanceRecord();
            Main.studentIdOrder.incrementAndGet();







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
                studentAttendance.add("ELA class");
            }else if(!Main.wentToMath[studentID] && MathTeacher.mathClassSession.size() < 6){
                MathTeacher.mathClassSession.add(studentID);
                MathTeacher.availableMathSeats.acquire();
                Main.wentToMath[studentID] = true;
                msg("went Math Class");
                studentAttendance.add("Math class");
            }else {
                msg("playing in the yard");
                studentAttendance.add("waited at the yard");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void goToOtherClasses(){
        while(!Main.wentToMath[studentID] || !Main.wentToELA[studentID]){
            //try {
                //mutex1.acquire();
                if (!Main.wentToELA[studentID] &&  currEla.get()< 6) {
                    currEla.incrementAndGet();
                    Main.wentToELA[studentID] = true;
                    msg("went ELA Class");
                    studentAttendance.add("ELA class");
                } else if (!Main.wentToMath[studentID] && currMath.get() < 6) {
                    currMath.incrementAndGet();
                    Main.wentToMath[studentID] = true;
                    msg("went Math Class");
                    studentAttendance.add("Math class");
                } else {
                    msg("playing in the yard");
                    studentAttendance.add("waited at the yard");
                    currEla.decrementAndGet();
                    currMath.decrementAndGet();

                }
                //mutex1.release();

           // }catch (InterruptedException e) {
              //  e.printStackTrace();
           // }
        }
    }
    private void provideAttendanceRecord(){
        String record = "ATTENDANCE : ";
        while(!studentAttendance.isEmpty()){
            if(Main.hasCovid[studentID]){
                break;
            }
            record+= studentAttendance.poll() + " -> ";


        }
        if(Main.hasCovid[studentID]){
            record+= "SENT HOME DUE TO COVID";
        }
        msg(record);
    }

    public void lineUpStudentsInOrder(){
        while (Main.studentIdOrder.get() < studentID && Main.studentIdOrder.get() < Main.numStudents ){
            try {
                if (Main.hasCovid[Main.studentIdOrder.get()]) {
                    Main.studentIdOrder.incrementAndGet();
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){

            }
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
