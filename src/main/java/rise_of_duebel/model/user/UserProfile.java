package rise_of_duebel.model.user;

import rise_of_duebel.model.entity.impl.EntityPlayer;

public class UserProfile {

    private double time;
    private boolean run;
    private int deaths;
    private final EntityPlayer player;

    public UserProfile(EntityPlayer player) {
        this.player = player;
        this.time = 0;
        this.run = false;
        this.deaths = 0;
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

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        deaths++;
    }

    public void resetDeaths() {
        deaths = 0;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}
