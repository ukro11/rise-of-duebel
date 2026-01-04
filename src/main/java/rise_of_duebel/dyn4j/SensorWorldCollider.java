package rise_of_duebel.dyn4j;

import rise_of_duebel.graphics.level.spawner.ObjectIdResolver;
import rise_of_duebel.graphics.map.GsonMap;

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
