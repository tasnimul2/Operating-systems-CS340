import javax.swing.table.AbstractTableModel;
import java.util.concurrent.atomic.AtomicInteger;

public class Student implements Runnable{
    public volatile int studentID;
    public static long time = System.currentTimeMillis();

    public Student(int id){
        this.studentID = id;
    }

    @Override
    public void run() {
        try{
            AtomicInteger doesGoToNurseChance = new AtomicInteger((int)(Math.random()*(3)+0));
            Thread.sleep(1000);
            enterTheYard();
            fillOutQuestionnaire();
            //while(Main.flag[studentID] == false){}
            while(!Thread.currentThread().isInterrupted()){}
            if(Main.sendHome[studentID]){
                msg("is being sent home for forgetting to fill out questionnaire ");
                return;
            }

            goToNursesRoom();
            waitForTheNurseAndGetShot();
            checkHowManySick();
            waitForTeacher();
            checkHowManySick();
            findClassToAttend();


            //System.out.println("Running student " + studentID);
        }catch (InterruptedException e){
            System.out.println("Running student " + studentID + " INTERRUPTED");

        }

    }
    /** This method is used to identify the list of all students for the principle to use to determine
     * whether the student needs to see the nurse or go to class **/
    private void enterTheYard(){
        Main.yard[studentID] = 1;
    }

    /** This method randomly generates a number between 0 to 100. If that number is less than or equal to
     * 15 (ie. 15% chance), that student forgot their questionnaire . Otherwise they have it completed**/
    private void fillOutQuestionnaire(){
        if((int)(Math.random()*(101)+0) <= 15){
            Main.questionnaire[studentID] = false;
            msg("forgot to fill out questionnaire");
        }else{
            Main.questionnaire[studentID] = true;
            msg("filled out questionnaire");
        }
    }
    /** if the student was flagged by the principal to go to the nurses room,
     * then the student goes to the nurses room**/
    private void goToNursesRoom() {
        try {
            if (Main.goToNurse[studentID]) {
                msg("is going to the nurse's room");
                Thread.sleep(1000);
            }
        }catch (InterruptedException e){
            msg("arrived at the nurses office");
        }
    }
    /**  if the student was flagged by the principal to go to the nurses room,
     * the student waits to be flagged by the nurse. after being interrupted by the nurse, if a number
     * below or equal to 3 is generated out of 100 (3% chance) the student has covid
     * other wise increase priority and go to class**/
    private void waitForTheNurseAndGetShot() {
        try {
            if (Main.goToNurse[studentID]) {
                msg("waiting for the nurse");
                while(!Thread.currentThread().isInterrupted()){}
                msg("received a shot from the nurse");
                if((int)(Math.random()*(101)+0) <= 3){
                    msg("is tested POSITIVE for covid, and is being sent home");
                    Main.numSick.incrementAndGet();
                    return;
                }else{
                    Thread.currentThread().setPriority(8);
                    msg("is rushing to get to class");
                    Thread.sleep(1000);


                }
            }
        }catch (Exception e){
            msg("is almost there to the class, from the nurses room");
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
            Main.classRoomQueue.add(studentID);
        }
    }
    /** wait for the teachers to come outside of the sleep state to start the lesson
     * When lessons start, students lose enthusiasm **/
    private void waitForTeacher(){
        msg("waiting for teacher to arrive");
        while(Main.teacher1.getState() == Thread.State.TIMED_WAITING ||
                Main.teacher2.getState() == Thread.State.TIMED_WAITING ){}
        while (!Thread.currentThread().isInterrupted()) { }
        loseEnthusiasm();
    }

    /** checks if enough people are sick. If they are, then terminate all student processes **/
    private void checkHowManySick(){
        if(Main.numSick.get() > Main.maxSick){
            msg("is going home because too many students have COVID");
            return;
        }
    }
    /** simulates loss of enthusiasm by yield**/
    private void loseEnthusiasm(){
        Thread.yield();
        Thread.yield();
        msg("has lost enthusiasm");
    }

    /** looks to see which class is open. If it is open, then attend that class and increment its capacity **/
    private void findClassToAttend(){
        if(!Main.wentToELA[studentID] && Main.currSizeELA.intValue() <= Main.maxClassroomSize){
            Main.wentToELA[studentID] = true;

            try{
                Main.currSizeELA.incrementAndGet();
                Main.teacher1.join();
            }catch (InterruptedException e){
                msg("Interrupted by Teacher ,ELA");
            }


            //msg("went to ELA Class");
        }else if(!Main.wentToMath[studentID] && Main.currSizeMath.intValue() <= Main.maxClassroomSize){
            Main.wentToMath[studentID] = true;

            try{
                Main.currSizeMath.incrementAndGet();
                Main.teacher2.join();
            }catch (InterruptedException e){
                msg("Interrupted by Teacher,Math");
            }

            //msg("went to Math Class");
        }else if (!Main.physEdRoom.contains(studentID) ){
            Main.physEdRoom.add(studentID);
            try{
                Main.teacher2.join();
            }catch (InterruptedException e){
                msg("Interrupted by Teacher,PhysEd");
            }
            //msg("went to Physical Education");
        }


    }

    private void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] Student# "+studentID+": "+m);
    }

}
