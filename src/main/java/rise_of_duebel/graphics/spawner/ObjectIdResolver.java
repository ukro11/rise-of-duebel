package rise_of_duebel.graphics.spawner;

import java.security.InvalidParameterException;

public class ObjectIdResolver {

    private final String id;
    private MapSpawner type;
    private String spawnerType;
    private int index;

    public ObjectIdResolver(String id) {
        this.id = id;
        String[] split = id.split("_");
        if (split.length != 3) throw new InvalidParameterException(String.format("Invalid spawner id (%s)", this.id));

        switch (split[0].toLowerCase()) {
            case "table": {
                this.type = MapSpawner.TABLE;
                break;
            }
        }
        this.spawnerType = split[1];
        this.index = Integer.parseInt(split[2]);
    }

    // table_normal_13
    public String getRawId() {
        return this.id;
    }

    // table (MapSpawner.TABLE)
    public MapSpawner getType() {
        return this.type;
    }

    // normal
    public String getSpawnerType() {
        return this.spawnerType;
    }

    // 13
    public int getIndex() {
        return this.index;
    }

    @Override
    public String toString() {
        return this.getRawId();
    }

    public enum MapSpawner {
        TABLE("table");

        private String name;

        private MapSpawner(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
