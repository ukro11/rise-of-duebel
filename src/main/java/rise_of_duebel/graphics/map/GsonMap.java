package rise_of_duebel.graphics.map;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GsonMap {

    private int width;
    private int height;
    private List<Tileset> tilesets;
    private List<Layer> layers;

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<Tileset> getTilesets() {
        return this.tilesets;
    }

    public List<Layer> getLayers() {
        return this.layers;
    }

    public class Tile {
        private int id;
        private List<TileAnimationFrame> animation;
        private Property loop;

        public int getId() {
            return this.id;
        }

        public List<TileAnimationFrame> getAnimation() {
            return this.animation;
        }

        public Property getLoop() {
            return this.loop;
        }
    }

    public static class TileAnimationFrame {
        private int tileid;
        private int duration;

        public int getTileId() {
            return this.tileid;
        }

        public int getDuration() {
            return this.duration;
        }
    }

    public class Tileset {
        @SerializedName("firstgid")
        private int firstGid;
        private String image;
        @SerializedName("imagewidth")
        private int imageWidth;
        @SerializedName("imageheight")
        private int imageHeight;
        @SerializedName("tilewidth")
        private int tileWidth;
        @SerializedName("tileheight")
        private int tileHeight;
        @SerializedName("tilecount")
        private int tileCount;
        private List<Tile> tiles;

        private String path;

        public int getFirstGid() {
            return this.firstGid;
        }

        public String getImage() {
            return this.image;
        }

        public int getImageWidth() {
            return this.imageWidth;
        }

        public int getImageHeight() {
            return this.imageHeight;
        }

        public int getTileWidth() {
            return this.tileWidth;
        }

        public int getTileHeight() {
            return this.tileHeight;
        }

        public int getTileCount() {
            return this.tileCount;
        }

        public List<Tile> getTiles() {
            return this.tiles;
        }

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public class Layer {
        private String name;
        private String type;
        private int width;
        private int height;
        private List<ObjectCollider> objects;
        private List<Chunk> data;
        private List<Chunk> chunks;

        public String getName() { return this.name; }
        public String getType() { return this.type; }
        public int getWidth() { return this.width; }
        public int getHeight() { return this.height; }
        public List<ObjectCollider> getObjects() { return this.objects; }
        public List<Chunk> getChunks() { return this.chunks; }
        public List<Chunk> getData() { return data; }

        /*@Override
        public String toString() {
            return "Layer{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", objects=" + objects +
                ", chunks=" + chunks +
                ", layers=" + layers +
            '}';
        }*/
    }

    public class Chunk {
        private int x;
        private int y;
        private int width;
        private int height;
        private List<Integer> data;

        public int getX() { return x; }
        public int getY() { return y; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public List<Integer> getData() { return data; }
    }

    public class ObjectCollider {
        private String name;
        private int x;
        private int y;
        private int width;
        private int height;
        private List<Polygon> polygon;
        private boolean ellipse;
        private boolean visible;
        private List<Property> properties;

        public String getName() {
            return this.name;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public List<Polygon> getPolygon() {
            return this.polygon;
        }

        public Boolean isEllipse() {
            return this.ellipse;
        }

        public boolean isVisible() {
            return this.visible;
        }

        public List<Property> getProperties() { return this.properties; }
    }

    public class Polygon {
        private int x;
        private int y;

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }
    }

    public class Property {
        private String name;
        private String type;
        private Object value;

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public Object getValue() {
            return this.value;
        }
    }
}
