import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static long time = System.currentTimeMillis();
    public static Thread[] students;
    public static volatile int[] yard;
    public static volatile boolean[] questionnaire;
    public static Thread principal;
    public static Thread nurse;
    public static Thread teacher1;
    public static Thread teacher2;
    public static volatile boolean[] sendHome;
    public static volatile boolean[] goToNurse;
    public static volatile boolean[] wentToELA;
    public static volatile boolean[] wentToMath;
    public static volatile HashSet<Integer> elaClassRoom;
    public static volatile HashSet<Integer> mathClassRoom;
    public static volatile HashSet<Integer> physEdRoom;
    public static volatile Queue<Integer> nursesRoomQueue;
    public static volatile Queue<Integer> classRoomQueue;
    public static volatile TreeSet<Integer> attendance ;
    public static volatile int maxSick = 2;
    public static volatile int maxClassroomSize = 4;
    public static AtomicInteger numSick = new AtomicInteger(0);
    public static AtomicInteger currSizeELA = new AtomicInteger(0);
    public static AtomicInteger currSizeMath = new AtomicInteger(0);

    /**
     * The main activity initializes the Arrays and Threads.
     * Then it starts each thread.
     * START READING CODE FROM STUDENT THREAD TO BETTER UNDERSTAND CODE BASE.
     **/
    public static void main(String[] args){
        int numStudents = 20;
        if(args.length !=0){
            numStudents = Integer.parseInt(args[0]);
        }

        students = new Thread[numStudents];
        questionnaire = new boolean[numStudents];
        sendHome = new boolean[numStudents];
        yard = new int[numStudents];
        goToNurse = new boolean[numStudents];
        wentToELA = new boolean[numStudents];
        wentToMath = new boolean[numStudents];
        elaClassRoom = new HashSet<>();
        mathClassRoom = new HashSet<>();
        physEdRoom = new HashSet<>();
        nursesRoomQueue = new LinkedList<>();//will hold the student id of students who need to go to nurse.
        classRoomQueue = new LinkedList<>();//will hold the student id of students who need to go to class.
        attendance = new TreeSet<>();

        principal = new Thread(new Principal("Principal"));
        nurse = new Thread(new Nurse("Nurse"));
        teacher1 = new Thread(new ElaTeacher("ELA Teacher"));
        teacher2 = new Thread(new MathTeacher("Math Teacher"));


        for(int i = 0; i < students.length; i++){
            students[i] = new Thread(new Student(i));
        }


        for(int i = 0; i < students.length; i++){
            students[i].start();

        }
        principal.start();
        nurse.start();
        teacher1.start();
        teacher2.start();

        for(int i = 0; i < students.length; i++){
            try {
                students[i].join();
            }catch (InterruptedException e){
                System.out.println("Student " +i + "is Interrupted in Main");
            }

        }
        try {
            teacher1.join();
            teacher2.join();
            principal.join();

        }catch (InterruptedException e){
            System.out.println("Teacher  is Interrupted in Main");
        }

        System.out.println(  "ELA Teacher is leaving");
        System.out.println( "Math Teacher is leaving");
        System.out.println("principal is leaving");
        System.out.println("SCHOOL FINISHED");



    }

}
