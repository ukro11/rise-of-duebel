package rise_of_duebel.model.entity.mobs.ai;

import rise_of_duebel.model.entity.Entity;
import rise_of_duebel.model.entity.mobs.EntityMob;

public abstract class TargetGoal<T extends Entity<?>> extends Goal {

    public TargetGoal(EntityMob<?> mob) {
        this(mob, true);
    }

    public TargetGoal(EntityMob<?> mob, boolean canAbort) {
        super(mob, canAbort);
    }

    /*public abstract void start(T target);
    public abstract void stop(T target);
    public abstract void update(T target, double dt);
    public abstract boolean trigger(T target);*/
}
