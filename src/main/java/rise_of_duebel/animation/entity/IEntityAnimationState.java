package rise_of_duebel.animation.entity;

import rise_of_duebel.animation.IAnimationState;
import rise_of_duebel.model.entity.EntityDirection;

public interface IEntityAnimationState extends IAnimationState {
    EntityDirection getDirection();
    EntityState getState();
}
