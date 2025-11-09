package project_base.graphics.map;

import KAGO_framework.view.DrawTool;
import com.google.gson.Gson;
import project_base.Config;
import project_base.graphics.IOrderRenderer;
import project_base.model.scene.GameScene;
import project_base.physics.BodyType;
import project_base.physics.Collider;
import project_base.physics.colliders.ColliderCircle;
import project_base.physics.colliders.ColliderPolygon;
import project_base.physics.colliders.ColliderRectangle;
import project_base.utils.Vec2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public abstract class TileMap {

    private GsonMap map;
    private String fileName;
    private final HashMap<GsonMap.Tileset, Batch> batches;
    private final List<String> staticLayers;
    private final List<String> staticLayersAfterPlayer;
    private final List<Quad> staticQuads;
    private final List<Quad> staticQuadsAfterPlayer;

    public TileMap(String fileName, List<String> staticLayers, List<String> staticLayersAfterPlayer) {
        String[] f = fileName.split("/");
        this.fileName = f[f.length - 1];
        this.staticLayers = staticLayers;
        this.staticLayersAfterPlayer = staticLayersAfterPlayer;
        InputStream fileStream = getClass().getResourceAsStream("/graphic" + fileName);
        if (fileStream == null) throw new NullPointerException("The map you want to import does not exist");
        Gson gson = new Gson();
        this.map = gson.fromJson(new InputStreamReader(fileStream, StandardCharsets.UTF_8), GsonMap.class);
        this.batches = new HashMap<>();
        this.staticQuads = new ArrayList<>();
        this.staticQuadsAfterPlayer = new ArrayList<>();
        this.load();
    }

    /***
     * Callback, wird aufgerufen, wenn die geladene Map einen Collider laden will
     * @param layer Der Layer von Tilemap, der den Collider lädt
     * @param objCollider Das Tilemap-Collider-Object (gibt Zugriff auf die Properties)
     * @param collider Der erstellte Collider vom Framework
     */
    public abstract void loadCollider(GsonMap.Layer layer, GsonMap.ObjectCollider objCollider, Collider collider);

    private void load() {
        for (GsonMap.Layer layer : this.map.getLayers()) {
            if (layer.getType().equals("tilelayer")) {
                if (layer.getChunks() != null && !layer.getChunks().isEmpty()) {
                    for (GsonMap.Chunk chunk : layer.getChunks()) {
                        this.loadChunk(layer, chunk);
                    }
                }
            } else if (layer.getType().equals("objectgroup")) {
                for (GsonMap.ObjectCollider o : layer.getObjects()) {
                    if (!o.isVisible()) continue;
                    Collider collider = null;

                    if (o.getPolygon() != null) {
                        Vec2[] vertices = new Vec2[o.getPolygon().size()];
                        for (int i = 0; i < vertices.length; i++) {
                            var v = o.getPolygon().get(i);
                            vertices[i] = new Vec2(v.getX(), v.getY());
                        }
                        collider = new ColliderPolygon(String.format("polygon-%s-%d", layer.getName(), layer.getObjects().indexOf(o) + 1), BodyType.STATIC, o.getX(), o.getY(), vertices);

                    } else if (o.isEllipse()) {
                        var radius = o.getWidth() / 2;
                        collider = new ColliderCircle(
                            String.format("circle-%s-%d", layer.getName(), layer.getObjects().indexOf(o) + 1),
                            BodyType.STATIC, o.getX(), o.getY(), radius
                        );

                    } else {
                        collider = new ColliderRectangle(String.format("rectangle-%s-%d", layer.getName(), layer.getObjects().indexOf(o) + 1), BodyType.STATIC, o.getX(), o.getY(), o.getWidth(), o.getHeight());
                    }

                    if (layer.getName().equals("sensor")) {
                        collider.setSensor(true);
                        collider.setColliderClass("map_sensor");

                    } else {
                        collider.setColliderClass("map");
                    }

                    this.loadCollider(layer, o, collider);
                }
            }
        }
    }

    private void loadChunk(GsonMap.Layer layer, GsonMap.Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkY = chunk.getY();

        for (int y = 0; y < chunk.getHeight(); y++) {
            for (int x = 0; x < chunk.getWidth(); x++) {
                int index = y * chunk.getWidth() + x;
                int gid = chunk.getData().get(index);
                if (gid > 0) {
                    GsonMap.Tileset currentTileset = this.findTilesetForGid(gid);
                    if (currentTileset == null) continue;
                    if (!this.batches.containsKey(currentTileset)) {
                        try {
                            String path = currentTileset.getImage().replace("sprites", "/graphic/map/sprites").toString();
                            BufferedImage image = ImageIO.read(getClass().getResource(path));

                            if (layer.getName().equals("light")) {
                                double factor = 1.6d;
                                for (int imageX = 0; x < image.getWidth(); x++) {
                                    for (int imageY = 0; y < image.getHeight(); y++) {
                                        // Hole den Farbwert des aktuellen Pixels
                                        Color originalColor = new Color(image.getRGB(imageX, imageY));

                                        // Berechne den neuen Farbwert, indem du den Faktor anwendest
                                        int r = Math.min((int)(originalColor.getRed() * factor), 255);
                                        int g = Math.min((int)(originalColor.getGreen() * factor), 255);
                                        int b = Math.min((int)(originalColor.getBlue() * factor), 255);

                                        // Setze den neuen Farbwert in das neue Bild
                                        image.setRGB(imageX, imageY, new Color(r, g, b).getRGB());
                                    }
                                }
                            }

                            currentTileset.setPath(path);
                            this.batches.put(currentTileset, new Batch(
                                    image, currentTileset.getImage(), currentTileset, layer
                            ));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    double tilesInX = currentTileset.getImageWidth() / currentTileset.getTileWidth();
                    double offsetGid = (gid - currentTileset.getFirstGid());

                    double tileX = Math.floor((chunkX + x) * currentTileset.getTileWidth());
                    double tileY = Math.floor((chunkY + y) * currentTileset.getTileHeight());

                    Quad quad = new Quad(
                            this.batches.get(currentTileset),
                            gid,
                            tileX,
                            tileY,
                            (int) (offsetGid % tilesInX * currentTileset.getTileWidth()),
                            (int) (currentTileset.getTileHeight() * Math.floor(offsetGid / tilesInX)),
                            currentTileset.getTileWidth(),
                            currentTileset.getTileHeight()
                    );

                    if (this.staticLayers.contains(layer.getName())) {
                        this.staticQuads.add(quad);

                    } else if (this.staticLayersAfterPlayer.contains(layer.getName())) {
                        this.staticQuadsAfterPlayer.add(quad);

                    } else {
                        GameScene.getInstance().getRenderer().register(quad);
                    }
                }
            }
        }
    }

    protected GsonMap.Tile getTile(GsonMap.Tileset tileset, int tileId) {
        if (tileset.getTiles() == null) return null;

        return tileset.getTiles().stream().filter(t -> t.getId() == tileId).findFirst().orElse(null);
    }

    protected boolean isTileAnimated(GsonMap.Tile tile) {
        return tile.getAnimation() != null;
    }

    private BufferedImage getAnimatedTileImage(GsonMap.Tileset tileset, BufferedImage image, int finalTileId, int elapsedTime) {
        if (tileset.getTiles() == null) return null;

        GsonMap.Tile tile = tileset.getTiles().stream()
                .filter(t -> finalTileId == t.getId())
                .findFirst()
                .orElse(null);

        int tileId = finalTileId;

        if (!this.isTileAnimated(tile)) return null;

        if (tile != null && tile.getAnimation() != null) {
            List<GsonMap.TileAnimationFrame> frames = tile.getAnimation();
            int totalDuration = frames.stream().mapToInt(f -> f.getDuration()).sum();
            int currentTime = elapsedTime % totalDuration;
            int accumulatedTime = 0;

            for (GsonMap.TileAnimationFrame frame : frames) {
                accumulatedTime += frame.getDuration();
                if (currentTime < accumulatedTime) {
                    tileId = frame.getTileId();
                    break;
                }
            }
        } else {
            return null;
        }

        int tilesPerRow = tileset.getImageWidth() / tileset.getTileWidth();
        int tileX = (tileId % tilesPerRow) * tileset.getTileWidth();
        int tileY = (tileId / tilesPerRow) * tileset.getTileHeight();

        return image.getSubimage(tileX, tileY, tileset.getTileWidth(), tileset.getTileHeight());
    }

    private GsonMap.Tileset findTilesetForGid(int gid) {
        for (GsonMap.Tileset tileset : this.map.getTilesets()) {
            if (gid >= tileset.getFirstGid() &&
                    gid < tileset.getFirstGid() + tileset.getTileCount()) {
                return tileset;
            }
        }
        return null;
    }

    public void draw(DrawTool drawTool) {
        Graphics gr = drawTool.getGraphics2D();
        for (Quad quad : this.staticQuads) {
            if (this.inView(quad)) {
                gr.drawImage(quad.getQuadImage(), (int) quad.getX(), (int) quad.getY(), (int) quad.getWidth(), (int) quad.getHeight(), null);
            }
        }
    }

    public void drawAfterPlayer(DrawTool drawTool) {
        Graphics gr = drawTool.getGraphics2D();
        for (Quad quad : this.staticQuadsAfterPlayer) {
            if (this.inView(quad)) {
                gr.drawImage(quad.getQuadImage(), (int) quad.getX(), (int) quad.getY(), (int) quad.getWidth(), (int) quad.getHeight(), null);
            }
        }
    }

    private static boolean inView(Quad quad) {
        int quadSize = 32;
        if (quad.getX() >= GameScene.getInstance().getCameraRenderer().getX() / GameScene.getInstance().getCameraRenderer().getZoom() - quadSize && quad.getY() >= GameScene.getInstance().getCameraRenderer().getY() / GameScene.getInstance().getCameraRenderer().getZoom() - quadSize) {
            if (quad.getX() + quad.getWidth() <= (GameScene.getInstance().getCameraRenderer().getX() + Config.WINDOW_WIDTH) / GameScene.getInstance().getCameraRenderer().getZoom() + quadSize &&
                    quad.getY() + quad.getHeight() <= (GameScene.getInstance().getCameraRenderer().getY() + Config.WINDOW_HEIGHT) / GameScene.getInstance().getCameraRenderer().getZoom() + quadSize) {
                return true;
            }
        }
        return false;
    }

    public String getFileName() {
        return this.fileName;
    }

    public class Batch {
        private BufferedImage image;
        private String imagePath;
        private GsonMap.Tileset tileset;
        private GsonMap.Layer layer;

        public Batch(BufferedImage image, String imagePath, GsonMap.Tileset tileset, GsonMap.Layer layer) {
            this.image = image;
            this.imagePath = imagePath;
            this.tileset = tileset;
            this.layer = layer;
        }

        public BufferedImage getImage() {
            return this.image;
        }

        public String getImagePath() {
            return this.imagePath;
        }

        public GsonMap.Tileset getTileset() {
            return this.tileset;
        }

        public GsonMap.Layer getLayer() {
            return this.layer;
        }
    }

    public class Quad implements IOrderRenderer {
        private Batch batch;
        private BufferedImage quadImage;
        private int tileId;
        private GsonMap.Tile tile;
        private int gid;
        private double x;
        private double y;
        private double quadX;
        private double quadY;
        private double width;
        private double height;

        public Quad(Batch batch, int gid, double x, double y, int quadX, int quadY, int width, int height) {
            this.batch = batch;
            this.quadImage = batch.getImage().getSubimage(quadX, quadY, width, height);
            this.gid = gid;
            this.tileId = gid - batch.getTileset().getFirstGid();
            this.tile = getTile(batch.getTileset(), gid - batch.getTileset().getFirstGid());
            this.x = x;
            this.y = y;
            this.quadX = quadX;
            this.quadY = quadY;
            this.width = width;
            this.height = height;
        }

        public Quad(Batch batch, int gid, double x, double y, BufferedImage tileImage, int width, int height) {
            this.batch = batch;
            this.quadImage = tileImage;
            this.gid = gid;
            this.tileId = gid - batch.getTileset().getFirstGid();
            this.tile = getTile(batch.getTileset(), gid - batch.getTileset().getFirstGid());
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean shouldRender() {
            return inView(this);
        }

        @Override
        public double zIndex() {
            return this.getY();
        }

        public Batch getBatch() {
            return this.batch;
        }

        public BufferedImage getQuadImage() {
            if (this.tile != null && isTileAnimated(this.tile)) {
                return getAnimatedTileImage(this.batch.getTileset(), this.batch.getImage(), this.tileId, (int) System.currentTimeMillis());
            }

            return this.quadImage;
        }

        public int getGid() {
            return this.gid;
        }

        public double getX() {
            return this.x;
        }


        public double getY() {
            return this.y;
        }

        @Override
        public void draw(DrawTool drawTool) {
            drawTool.getGraphics2D().drawImage(this.getQuadImage(), (int) this.getX(), (int) this.getY(), (int) this.getWidth(), (int) this.getHeight(), null);
        }

        public double getQuadX() {
            return this.quadX;
        }

        public double getQuadY() {
            return this.quadY;
        }

        public double getWidth() {
            return this.width;
        }

        public double getHeight() {
            return this.height;
        }
    }
}
