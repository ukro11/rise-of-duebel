package rise_of_duebel.model.entity.mobs;

import KAGO_framework.model.abitur.datenstrukturen.AbiList;
import KAGO_framework.view.DrawTool;
import rise_of_duebel.animation.entity.IEntityAnimationState;
import rise_of_duebel.model.entity.Entity;
import rise_of_duebel.model.entity.mobs.ai.Goal;
import rise_of_duebel.physics.Collider;

public abstract class EntityMob<T extends Enum<T> & IEntityAnimationState> extends Entity<T> {

    private final AbiList<Goal> goals;
    private MobType type;

    public EntityMob(MobType type, Collider body, double x, double y, double width, double height) {
        super(body, x, y, width, height);
        this.goals = new AbiList<>();
        this.type = type;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        var goal = this.goals.getContent();
        for (int i = 0; i < this.goals.size(); i++) {
            this.iterateGoal(dt, goal);
            this.goals.next();
            goal = this.goals.getContent();
        }
    }

    private void iterateGoal(double dt, Goal goal) {
        if (goal.trigger()) {
            if (!goal.started) {
                goal.started = true;
                goal.start();
            }
            goal.update(dt);

        } else if (goal.started) {
            goal.stop();
            goal.started = false;
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        super.draw(drawTool);
    }

    public MobType getType() {
        return this.type;
    }
}
