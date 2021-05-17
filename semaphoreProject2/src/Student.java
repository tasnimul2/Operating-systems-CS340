import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Student implements  Runnable{
    public volatile int studentID;
    public static long time = System.currentTimeMillis();
    public static AtomicInteger currEla,currMath;
    private Queue<String> studentAttendance ;



    /** CONSTRUCTOR : the student id keeps track of the student. THe currELa and CurrMath variables keep tack of the number of
     * students in the respective classes. this allows only 6 or less students to be in class at a time
     * studentAttendance queue lists the attendance order of the students **/
    public Student(int id){
        this.studentID = id;
        currEla = new AtomicInteger(0);
        currMath = new AtomicInteger(0);
        studentAttendance = new LinkedList<>();
    }

    /** Run Method: The student first goes to the yard, then enters the school. if they were late, terminate the thread
     * then wait for principal to decide whether this student should go to nurse or class
     * if the student had to go to nurse and had covid, terminate.
     * wait for the teacher to arrive. When the teacher arrives, find a class to attend
     * once done with the first session, attend the second session. Then wait for the principal to signal the student to give attendance
     * then the students line up in order to give attendance. (this method bugs out if a student has covid so I commented it out)
     * DETAILS OF THE CODE IS ABOVE EACH METHOD DECLARATION**/
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
            //lineUpStudentsInOrder();//makes sure that the students are listed in the order of their student id. please uncomment to see how it works.
            provideAttendanceRecord();
            Main.studentIdOrder.incrementAndGet();//used by the lineUpStudentsInOrder() ,method

        } catch (InterruptedException e) {
            System.out.println("Student " + studentID + " is interrupted");
        }

    }

    /** simulate the student going to the yard by setting the value of yard at the studentID index to 1.
     * the principal will go through the yard and let students it. This also helps identify late students**/
    private void goToYard(){
        msg("On the way to the school yard");
        Main.yard[studentID] = 1;
    }

    /** checks if the student was late by checking if yard[studentID] is 1. If it is 2, student is not late
     * since when the principal let students in, it changed the 1 to a 2.
     * in the main method, if we start principal thread before all of the student threads, almost all students will be late**/
    private boolean wasStudentLate(){
        return Main.yard[studentID] == 1;
    }

    /** this method checks which class the student did not yet attend. ELA or Math and makes sure the class has
     * only 6 students. The students then wait for the teachers to signal the end of class
     * via the availableELaSeats semaphore and availableMathSeats semaphore. When the students acquire the seat, they are in the class
     * when the teacher signals available[ELA/Math]Seats semaphore  to symbolize the teacher telling the students to get off the seats
     * the student then updates the attendance to give to the principal later on.**/
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
    /** same as the findAClass() method except it loops until the student as went to both ELA and Math class**/
    private void goToOtherClasses(){
        while(!Main.wentToMath[studentID] || !Main.wentToELA[studentID]){
            //try {
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
           // }catch (InterruptedException e) {
              //  e.printStackTrace();
           // }
        }
    }
    /** this method loops though the attendance queue and gives the student attendance
     * checking if student has covid is only useful if lineUpStudentsInOrder() is not commented out
     * since that method has no concept of which terminated treads as it uses a separate int value
     * that is incremented from 0. hence, it will take into account all students. That is why the if
     * conditions for covid is here. **/
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

    /** This method is commented out BUT , it works 95% of the time. If too many students have covid, it seems to not work.
     * but 95% of the time, it works fine (if only 1 or 2 students have covid). Uncomment this method (in run method for student thread)
     * and run the code again to check.
     * The way this method works is by using the value of studentIDOrder , which is initialized to 0 and watching if its value is less than studentID
     * this means initially only student 0 will be able to give attendance  since 0 !< 0. If its a student with a higher student ID, it will busy wait
     * in the while look until it gets its turn to give attendance. If studentIdOrder happens to be a number that represents a studentID that has covid
     * then do not busy wait and skip over that student**/
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

    /** simplified method for sleep to not do try catch too many times.**/
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
