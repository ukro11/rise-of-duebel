package duebel_level.dyn4j;

import duebel_level.graphics.level.ObjectIdResolver;
import duebel_level.graphics.map.GsonMap;

public class SensorWorldCollider extends WorldCollider {

    protected WorldCollider collider;

    public SensorWorldCollider(GsonMap.ObjectCollider data) {
        super(data);
    }

    public SensorWorldCollider(GsonMap.ObjectCollider data, ObjectIdResolver resolver) {
        super(data, resolver);
    }

    public WorldCollider getCollider() {
        return this.collider;
    }

    public void setCollider(WorldCollider collider) {
        this.collider = collider;
    }
}
