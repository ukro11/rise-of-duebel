package rise_of_duebel.model.entity.impl;

import rise_of_duebel.Wrapper;
import rise_of_duebel.animation.IAnimationState;
import rise_of_duebel.model.entity.Entity;
import rise_of_duebel.model.entity.impl.player.EntityPlayer;
import rise_of_duebel.model.sound.SoundManager;
import rise_of_duebel.physics.Collider;
import rise_of_duebel.utils.Vec2;

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
