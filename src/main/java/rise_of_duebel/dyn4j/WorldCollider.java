package rise_of_duebel.dyn4j;

import rise_of_duebel.graphics.level.spawner.ObjectIdResolver;
import rise_of_duebel.graphics.map.GsonMap;

import java.util.ArrayList;
import java.util.List;

public class WorldCollider extends ColliderBody {

    protected GsonMap.ObjectCollider data;
    protected ObjectIdResolver resolver;
    protected String layer;
    protected int zindex;
    protected List<WorldCollider> sensors;

    public WorldCollider(GsonMap.ObjectCollider data) {
        this(data, null);
    }

    public WorldCollider(GsonMap.ObjectCollider data, ObjectIdResolver resolver) {
        this.sensors = new ArrayList<>();
        this.data = data;
        this.resolver = resolver;
        this.setUserData(resolver == null ? "" : resolver.getRawId());
    }

    public WorldCollider getSensorByIndex(int index) {
        return this.sensors.size() <= index ? null : this.sensors.get(index);
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public void setZIndex(int zindex) {
        this.zindex = zindex;
    }

    public List<WorldCollider> getSensors() {
        return this.sensors;
    }

    public int getZIndex() {
        return this.zindex;
    }

    public GsonMap.ObjectCollider getData() {
        return this.data;
    }

    public ObjectIdResolver getResolver() {
        return this.resolver;
    }

    public String getLayer() {
        return this.layer;
    }
}
