package rise_of_duebel.model.entity.mobs;

import KAGO_framework.view.DrawTool;
import org.dyn4j.world.World;
import rise_of_duebel.animation.entity.IEntityAnimationState;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.model.entity.Entity;
import rise_of_duebel.model.entity.mobs.ai.Goal;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMob<T extends Enum<T> & IEntityAnimationState> extends Entity<T> {

    private final List<Goal> goals;
    private MobType type;

    public EntityMob(World<ColliderBody> world, MobType type, ColliderBody body, double x, double y, double width, double height) {
        super(world, body, x, y, width, height);
        this.goals = new ArrayList<>();
        this.type = type;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        for (Goal goal : this.goals) {
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
    }

    @Override
    public void draw(DrawTool drawTool) {
        super.draw(drawTool);
    }

    public MobType getType() {
        return this.type;
    }
}
