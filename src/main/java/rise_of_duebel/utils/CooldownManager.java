package rise_of_duebel.utils;

import java.util.concurrent.CopyOnWriteArrayList;

public class CooldownManager {

    private static final CopyOnWriteArrayList<CooldownManager> cooldowns = new CopyOnWriteArrayList<>();

    private double elapsed;
    private final double time;
    private boolean active = false;

    public CooldownManager(double time) {
        this.time = time;
        this.elapsed = time;
        CooldownManager.cooldowns.add(this);
    }

    private void updateCooldown(double dt) {
        if (this.active) {
            this.elapsed = Math.max(this.elapsed - dt, 0);
            if (this.elapsed == 0) {
                this.active = false;
                this.elapsed = this.time;
            }
        }
    }

    public void use(Runnable runnable) {
        if (!this.active) {
            this.active = true;
            runnable.run();
        }
    }

    public boolean use() {
        if (!this.active) {
            this.active = true;
            return true;
        }
        return false;
    }

    public double getTime() {
        return this.time;
    }

    public static void update(double dt) {
        for (CooldownManager cooldown : CooldownManager.cooldowns) {
            cooldown.updateCooldown(dt);
        }
    }
}
