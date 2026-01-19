package duebel_level.dyn4j;

import duebel_level.graphics.level.ObjectIdResolver;
import duebel_level.graphics.map.GsonMap;

import java.util.ArrayList;
import java.util.List;

public class WorldCollider extends ColliderBody {

    protected GsonMap.ObjectCollider data;
    protected ObjectIdResolver resolver;
    protected String layer;
    protected int zindex;
    protected List<WorldCollider> sensors;

    /**
     * Erstellt einen WorldCollider ohne Resolver.
     *
     * @param data Map-Collider-Daten
     */
    public WorldCollider(GsonMap.ObjectCollider data) {
        this(data, null);
    }

    /**
     * Erstellt einen WorldCollider und setzt UserData über den Resolver.
     *
     * @param data Map-Collider-Daten
     * @param resolver Resolver (kann null sein)
     */
    public WorldCollider(GsonMap.ObjectCollider data, ObjectIdResolver resolver) {
        this.sensors = new ArrayList<>();
        this.data = data;
        this.resolver = resolver;
        this.setUserData(resolver == null ? "" : resolver.getRawId());
    }

    /**
     * @param index Sensor-Index
     * @return Sensor oder null
     */
    public WorldCollider getSensorByIndex(int index) {
        return this.sensors.size() <= index ? null : this.sensors.get(index);
    }

    /**
     * @param layer Layername
     */
    public void setLayer(String layer) {
        this.layer = layer;
    }

    /**
     * @param zindex Z-Index
     */
    public void setZIndex(int zindex) {
        this.zindex = zindex;
    }

    /**
     * @return Liste der Sensor-Collider
     */
    public List<WorldCollider> getSensors() {
        return this.sensors;
    }

    /**
     * @return Z-Index
     */
    public int getZIndex() {
        return this.zindex;
    }

    /**
     * @return Map-Collider-Daten
     */
    public GsonMap.ObjectCollider getData() {
        return this.data;
    }

    /**
     * @return ObjectIdResolver (kann null sein)
     */
    public ObjectIdResolver getResolver() {
        return this.resolver;
    }

    /**
     * @return Layername
     */
    public String getLayer() {
        return this.layer;
    }
}
