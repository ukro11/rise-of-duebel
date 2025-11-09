package project_base.model.entity.impl;

import project_base.Wrapper;
import project_base.animation.IAnimationState;
import project_base.model.entity.Entity;
import project_base.model.entity.impl.player.EntityPlayer;
import project_base.model.sound.SoundManager;
import project_base.physics.Collider;
import project_base.utils.Vec2;

public abstract class EntityItem<T extends Enum<T> & IAnimationState> extends Entity<T> {

    protected EntityPlayer player;
    protected EntityItemLocation<EntityItem> location;
    protected double rotation;

    public EntityItem(Collider collider, double x, double y, double width, double height) {
        super(collider, x, y, width, height);
        this.body.setColliderClass("entity_item");
        this.offset = new Vec2();
    }

    public void onPick(EntityPlayer player) {
        if (this.location != null) this.location.removeItem(this);
        this.location = null;
        this.player = player;
        this.player.getBody().addChild(this.body);
        this.body.setPosition(this.player.getBody().getX() + this.body.getChildInstance().getOffsetX(), this.player.getBody().getY() + this.body.getChildInstance().getOffsetY());
        SoundManager.playSound(Wrapper.getSoundConstants().SOUND_PICKUP);
    }

    public void onDrop(EntityItemLocation location) {
        if (location == null) {
            this.logger.warn("If you place a plate on table that's null, nothing will happen (entityId: {})", this.id);
            return;
        }
        if (this.player != null) {
            this.player.getBody().removeChild(this.body.getChildInstance());
            this.player = null;
        }
        this.location = location;
        this.body.setPosition(location.getCollider().getX(), location.getCollider().getY());
        location.addItem(this);
        SoundManager.playSound(Wrapper.getSoundConstants().SOUND_PICKUP);
    }

    public EntityItemLocation getLocation() {
        return this.location;
    }

    public void setLocation(EntityItemLocation location) {
        this.location = location;
        this.positionItem(location);
    }

    public void positionItem(EntityItemLocation location) {
        this.rotation = location.getRotation();
    }
}
