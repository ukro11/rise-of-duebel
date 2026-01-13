package rise_of_duebel.model.user;

import rise_of_duebel.Wrapper;


public class UserProfile {

    private int deaths;
    private double timePlayed;
    private double time;
    private boolean paused;

    public UserProfile() {
        this.deaths = 0;
        this.timePlayed = 0;
        this.time = 0;
        this.paused = false;
    }

    public void update(double dt) {
        timePlayed = Wrapper.getTimer().getRunningTime();
            if (!paused) {
                time += dt;
            }
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

    public double getTimePlayed() {
        return timePlayed;
    }

    public void pauseTime() {
        paused = true;
    }
    public double getTime() {
        return time;
    }
    public void resetTime() {
        time = 0;
    }
    public void resumeTime() {
        paused = false;
    }
}