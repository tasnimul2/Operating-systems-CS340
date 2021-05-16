public class Principal implements Runnable{
    public static long time = System.currentTimeMillis();
    private String threadName;

    public Principal(String name){
        Thread.currentThread().setName(name);
        threadName = Thread.currentThread().getName();
    }

    @Override
    public void run() {
        msg("is on his way to let students in...");
        sleep(3000);
        letStudentsIn();
        msg("running");

    }

    private void letStudentsIn(){
        for(int i = 0; i < Main.yard.length;i++){
            Main.enterSchool.release();
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
