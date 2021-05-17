import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static volatile int numStudents;
    public static Thread[] students;
    public static Thread principal;
    public static Thread nurse;
    public static Thread elaTeacher;
    public static Thread mathTeacher;
    public static Semaphore enterSchool;
    public static Semaphore waitForPrincipalDecision;
    public static Semaphore allStudentsHaveDestination;
    public static Semaphore nursesRoom;
    public static Semaphore waitForTeacherToArrive;
    public static volatile Semaphore getAttendenceTaken;
    public static volatile int[] yard;
    public static volatile boolean[] goToNurse;
    public static volatile boolean[] hasCovid;
    public static volatile boolean[] wentToELA;
    public static volatile boolean[] wentToMath;
    public static volatile Queue<Integer> nursesRoomQueue;
    public static volatile Queue<Integer> classRoomQueue;
    public static AtomicInteger numStudentsWaiting;
    public static AtomicInteger studentIdOrder;

    /** LOOKING THE THE STUDENT THREAD FIRST WILL ALLOW YOU TO BETTER UNDERSTAND THE PROGRAM **/
    /** Main method is responsible for starting all the threads**/
    public static void main(String[] args){
        numStudents = 20;
        if(args.length != 0){
            numStudents = Integer.parseInt(args[0]);
        }

        students = new Thread[numStudents];
        principal = new Thread(new Principal("Principal"));
        nurse = new Thread(new Nurse("Nurse"));
        elaTeacher = new Thread(new ElaTeacher("ELA Teacher"));
        mathTeacher = new Thread(new MathTeacher("Math Teacher"));
        enterSchool = new Semaphore(0,true);
        waitForPrincipalDecision = new Semaphore(0,true);
        nursesRoom = new Semaphore(0,true);
        allStudentsHaveDestination = new Semaphore(0,true);
        waitForTeacherToArrive = new Semaphore(0,true);
        getAttendenceTaken = new Semaphore(0,true);
        yard = new int[numStudents];
        goToNurse = new boolean[numStudents];
        hasCovid = new boolean[numStudents];
        wentToMath = new boolean[numStudents];
        wentToELA = new boolean[numStudents];
        nursesRoomQueue = new LinkedList<>();//will hold the student id of students who need to go to nurse.
        classRoomQueue = new LinkedList<>();//will hold the student id of students who need to go to class.
        numStudentsWaiting = new AtomicInteger(0);
        studentIdOrder = new AtomicInteger(0);
        for(int i = 0; i < students.length; i++){
            students[i] = new Thread(new Student(i));
        }

        for(int i = 0; i < students.length;i++){
            students[i].start();
        }

        principal.start();
        nurse.start();
        elaTeacher.start();
        mathTeacher.start();




    }//end of main
}
