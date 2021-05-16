import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Main {
    public static Thread[] students;
    public static Thread principal;
    public static Thread nurse;
    public static Semaphore enterSchool;
    public static Semaphore waitForPrincipalDecision;
    public static Semaphore allStudentsHaveDestination;
    public static Semaphore nursesRoom;
    public static volatile int[] yard;
    public static volatile boolean[] goToNurse;
    public static volatile boolean[] hasCovid;
    public static volatile Queue<Integer> nursesRoomQueue;
    public static volatile Queue<Integer> classRoomQueue;

    public static void main(String[] args){
        int numStudents = 20;
        if(args.length != 0){
            numStudents = Integer.parseInt(args[0]);
        }

        students = new Thread[numStudents];
        principal = new Thread(new Principal("Principal"));
        nurse = new Thread(new Nurse("Nurse"));
        enterSchool = new Semaphore(0,true);
        waitForPrincipalDecision = new Semaphore(0,true);
        nursesRoom = new Semaphore(0,true);
        allStudentsHaveDestination = new Semaphore(0,true);
        yard = new int[numStudents];
        goToNurse = new boolean[numStudents];
        hasCovid = new boolean[numStudents];
        nursesRoomQueue = new LinkedList<>();//will hold the student id of students who need to go to nurse.
        classRoomQueue = new LinkedList<>();//will hold the student id of students who need to go to class.
        for(int i = 0; i < students.length; i++){
            students[i] = new Thread(new Student(i));
        }

        for(int i = 0; i < students.length;i++){
            students[i].start();
        }

        principal.start();
        nurse.start();




    }//end of main
}
