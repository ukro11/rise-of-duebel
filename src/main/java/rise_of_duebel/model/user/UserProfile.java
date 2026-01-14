package rise_of_duebel.model.user;

public class UserProfile {

    private double time;
    private boolean run;

    public UserProfile(){
        this.time = 0;
        this.run = false;
    }

    public void update(double dt){
       if (runTime()){
        time += dt;
       }
    }

    public void start() {
        this.time = 0;
        this.run = true;
    }

    public void pause() {
        this.run = false;
    }

    public boolean runTime(){
        return this.run;
    }

    public double getTime() {
        return time;
    }
}
