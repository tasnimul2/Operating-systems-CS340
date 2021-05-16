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
        } catch (InterruptedException e) {
            System.out.println("Student " + studentID + " is interrupted");
        }
        msg("running");
    }

    private void goToYard(){
        msg("On the way to the school yard");
        Main.yard[studentID] = 1;
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
