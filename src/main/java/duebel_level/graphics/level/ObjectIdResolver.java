package duebel_level.graphics.level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ObjectIdResolver {

    private static final Logger log = LoggerFactory.getLogger(ObjectIdResolver.class);
    private final String id;
    private String layer;
    private String type;
    private MapSpawner type2;
    private int index = -1;

    public ObjectIdResolver(String id) {
        this.id = id;
        String[] split = id.split("_");
        //if (split.length != 3) throw new InvalidParameterException(String.format("Invalid spawner id (%s)", this.id));

        if (split.length > 1) {
            this.layer = split[0].contains("$") ? split[0].split("\\$")[1] : split[0];
            Arrays.stream(MapSpawner.values()).forEach(m -> {
                if (split[1].toLowerCase().equals(m.getName())) {
                    this.type2 = m;
                }
            });
            this.type = split[1];
            //if (this.type2 == null) log.warn("Type not existing ({}), fix it or add it to MapSpawner (enum)", split[1]);
            this.index = Integer.parseInt(split[2]);
        }
    }

    public boolean isValid() {
        return (this.id != null || this.id.isBlank() || this.id.isEmpty()) && this.layer != null && this.type != null && this.index > -1;
    }

    // D$WORLD_PLATFORM_1
    public String getRawId() {
        return this.id;
    }

    // WORLD
    public String getLayer() {
        return this.layer;
    }

    public String getType() {
        return this.type;
    }

    // PLATFORM (MapSpawner.PLATFORM)
    public MapSpawner getTypeSpawner() {
        return this.type2;
    }

    // 1
    public int getIndex() {
        return this.index;
    }

    @Override
    public String toString() {
        return this.getRawId();
    }

    public enum MapSpawner {
        PLATFORM("platform"),
        SPIKE("spike");

        private String name;

        private MapSpawner(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
