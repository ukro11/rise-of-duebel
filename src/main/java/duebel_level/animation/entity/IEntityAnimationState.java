package duebel_level.animation.entity;

import duebel_level.animation.IAnimationState;
import duebel_level.model.entity.EntityDirection;

public interface IEntityAnimationState extends IAnimationState {
    EntityDirection getDirection();
    EntityState getState();
}
