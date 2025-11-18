package rise_of_duebel.model.entity.mobs;

import KAGO_framework.view.DrawTool;
import rise_of_duebel.animation.entity.IEntityAnimationState;
import rise_of_duebel.model.entity.Entity;
import rise_of_duebel.physics.Collider;

public abstract class EntityMob<T extends Enum<T> & IEntityAnimationState> extends Entity<T> {

    public EntityMob(Collider body, double x, double y, double width, double height) {
        super(body, x, y, width, height);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
    }

    @Override
    public void draw(DrawTool drawTool) {
        super.draw(drawTool);
    }
}
