package rise_of_duebel.model.entity.mobs.ai;

import rise_of_duebel.model.entity.mobs.EntityMob;

public abstract class Goal {

    protected final EntityMob<?> mob;
    protected final boolean canAbort;
    public boolean started = false;

    public Goal(EntityMob<?> mob) {
        this(mob, true);
    }

    public Goal(EntityMob<?> mob, boolean canAbort) {
        this.mob = mob;
        this.canAbort = canAbort;
    }

    public abstract void start();
    public abstract void stop();
    public abstract void update(double dt);
    public abstract boolean trigger();

    public EntityMob<?> getMob() {
        return this.mob;
    }

    public boolean canAbort() {
        return this.canAbort;
    }
}
