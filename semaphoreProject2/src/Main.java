import java.util.concurrent.Semaphore;

public class Main {
    public static Thread[] students;
    public static Thread principal;
    public static Semaphore enterSchool;
    public static volatile int[] yard;

    public static void main(String[] args){
        int numStudents = 20;
        if(args.length != 0){
            numStudents = Integer.parseInt(args[0]);
        }

        students = new Thread[numStudents];
        principal = new Thread(new Principal("Principal"));
        enterSchool = new Semaphore(0);
        yard = new int[numStudents];

        for(int i = 0; i < students.length; i++){
            students[i] = new Thread(new Student(i));
        }

        for(int i = 0; i < students.length;i++){
            students[i].start();
        }

        principal.start();


    }//end of main
}
