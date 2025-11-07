package project_base.model.entity.impl.player;

import project_base.model.entity.impl.EntityItemLocation;
import project_base.model.entity.impl.EntityItem;

public class PlayerInventory {

    private final EntityPlayer player;
    private EntityItem<?> item;

    public PlayerInventory(EntityPlayer player) {
        this.player = player;
    }

    public boolean hasItemInventory() {
        return this.item != null;
    }

    public EntityItem<?> getItemInHand() {
        return this.item;
    }

    public EntityPlate getItemAsPlate() {
        if (this.item != null) return (EntityPlate) this.item;
        return null;
    }

    public EntityFood<?> getItemAsFood() {
        if (this.item != null) return (EntityFood<?>) this.item;
        return null;
    }

    public void pickItem(EntityItem<?> item) {
        this.item = item;
        this.item.onPick(this.player);
    }

    public void dropItem(EntityItemLocation location) {
        this.item.onDrop(location);
        this.item = null;
    }
    public void removeItem(EntityItemLocation location) {
        this.item.onDrop(location);
        this.item = null;
    }
}
