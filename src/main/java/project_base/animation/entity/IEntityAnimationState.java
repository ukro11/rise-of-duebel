package project_base.animation.entity;

import project_base.animation.IAnimationState;
import project_base.model.entity.EntityDirection;

public interface IEntityAnimationState extends IAnimationState {
    EntityDirection getDirection();
    EntityState getState();
}
