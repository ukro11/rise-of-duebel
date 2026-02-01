package duebel_level.model.user;

public class ProfileStats {

    private final int level;
    private final int time;
    private final int deaths;

    public ProfileStats(int level, int time, int deaths) {
        this.level = level;
        this.time = time;
        this.deaths = deaths;
    }

    public int getLevel() {
        return this.level;
    }

    public int getTime() {
        return this.time;
    }

    public int getDeaths() {
        return this.deaths;
    }
}
