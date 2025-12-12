package rise_of_duebel.graphics.map;

import KAGO_framework.view.DrawTool;
import com.google.gson.Gson;
import rise_of_duebel.Config;
import rise_of_duebel.Wrapper;
import rise_of_duebel.graphics.CameraRenderer;
import rise_of_duebel.graphics.IOrderRenderer;
import rise_of_duebel.model.scene.GameScene;
import rise_of_duebel.physics.BodyType;
import rise_of_duebel.physics.Collider;
import rise_of_duebel.physics.colliders.ColliderCircle;
import rise_of_duebel.physics.colliders.ColliderPolygon;
import rise_of_duebel.physics.colliders.ColliderRectangle;
import rise_of_duebel.utils.CacheManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class TileMap {

    private GsonMap map;
    private Path directory;
    private String fileName;
    private final HashMap<GsonMap.Tileset, Batch> batches;
    private final List<String> batchLayers;
    private final List<String> batchLayersAfterPlayer;
    private final List<String> staticLayers;
    private final List<String> staticLayersAfterPlayer;
    private final List<Quad> batchQuads;
    private final List<Quad> batchQuadsAfterPlayer;
    private final List<Quad> staticQuads;
    private final List<Quad> staticQuadsAfterPlayer;

    private BufferedImage batchImage;
    private BufferedImage batchImageAfterPlayer;

    private final CameraRenderer camera;

    public TileMap(String fileName, List<String> staticLayers, List<String> staticLayersAfterPlayer, List<String> batchLayers, List<String> batchLayersAfterPlayer) {
        String[] f = fileName.split("/");
        this.directory = Path.of(fileName).getParent();
        this.fileName = f[f.length - 1];
        this.staticLayers = staticLayers;
        this.staticLayersAfterPlayer = staticLayersAfterPlayer;
        this.batchLayers = batchLayers;
        this.batchLayersAfterPlayer = batchLayersAfterPlayer;
        InputStream fileStream = getClass().getResourceAsStream("/graphic" + fileName);
        if (fileStream == null) throw new NullPointerException("The map you want to import does not exist");
        Gson gson = new Gson();
        this.map = gson.fromJson(new InputStreamReader(fileStream, StandardCharsets.UTF_8), GsonMap.class);
        this.batches = new HashMap<>();
        this.staticQuads = new ArrayList<>();
        this.staticQuadsAfterPlayer = new ArrayList<>();
        this.batchQuads = new ArrayList<>();
        this.batchQuadsAfterPlayer = new ArrayList<>();
        this.camera = GameScene.getInstance().getCameraRenderer();
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
            if (layer.getType().equals("tilelayer") && layer.isVisible()) {
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

        int mapWidthPx = this.map.getWidth() * this.map.getTileWidth();
        int mapHeightPx = this.map.getHeight() * this.map.getTileHeight();

        if (!this.batchLayers.isEmpty()) {
            this.batchImage = new BufferedImage(mapWidthPx, mapHeightPx, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = this.batchImage.createGraphics();
            for (Quad quad : this.batchQuads) {
                g.drawImage(quad.getQuadImage(), (int) quad.getX(), (int) quad.getY(), (int) quad.getWidth(), (int) quad.getHeight(), null);
            }
            g.dispose();
            //this.batchLayers.clear();
            //this.batchQuads.clear();
        }

        if (!this.batchLayersAfterPlayer.isEmpty()) {
            this.batchImageAfterPlayer = new BufferedImage(mapWidthPx, mapHeightPx, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = this.batchImageAfterPlayer.createGraphics();
            for (Quad quad : this.batchQuadsAfterPlayer) {
                g2.drawImage(quad.getQuadImage(), (int) quad.getX(), (int) quad.getY(), (int) quad.getWidth(), (int) quad.getHeight(), null);
            }
            g2.dispose();
            this.batchLayersAfterPlayer.clear();
            this.batchQuadsAfterPlayer.clear();
        }
    }

    private void loadChunk(GsonMap.Layer layer, GsonMap.Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkY = chunk.getY();

        for (int y = 0; y < chunk.getHeight(); y++) {
            for (int x = 0; x < chunk.getWidth(); x++) {
                int index = y * chunk.getWidth() + x;
                long gid = chunk.getData().get(index);
                if (gid > 0) {
                    GsonMap.Tileset currentTileset = this.findTilesetForGid(gid);
                    if (currentTileset == null) continue;
                    if (!this.batches.containsKey(currentTileset)) {
                        String path = this.directory.resolve(currentTileset.getImage()).toString();
                        BufferedImage image = CacheManager.loadImage(String.format("/graphic/%s", path));

                        if (layer.getName().equals("light")) {
                            double factor = 1.6d;
                            for (int imageX = 0; imageX < image.getWidth(); imageX++) {
                                for (int imageY = 0; imageY < image.getHeight(); imageY++) {
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

                    if ((quad.tile != null && quad.tile.getAnimation() != null) || this.staticLayers.contains("*") || this.staticLayers.contains(layer.getName())) {
                        this.staticQuads.add(quad);

                    } else if (this.staticLayersAfterPlayer.contains(layer.getName())) {
                        this.staticQuadsAfterPlayer.add(quad);

                    } else if (this.batchLayers.contains(layer.getName())) {
                        this.batchQuads.add(quad);

                    } else if (this.batchLayersAfterPlayer.contains(layer.getName())) {
                        this.batchQuadsAfterPlayer.add(quad);

                    } else {
                        GameScene.getInstance().getRenderer().register(quad);
                    }
                }
            }
        }
    }

    protected GsonMap.Tile getTile(GsonMap.Tileset tileset, long tileId) {
        if (tileset.getTiles() == null) return null;

        return tileset.getTiles().stream().filter(t -> t.getId() == tileId).findFirst().orElse(null);
    }

    protected boolean isTileAnimated(GsonMap.Tile tile) {
        return tile != null && tile.getAnimation() != null;
    }

    private BufferedImage getAnimatedTileImage(GsonMap.Tileset tileset, BufferedImage image, long finalTileId, int elapsedTime) {
        if (tileset.getTiles() == null) return null;

        GsonMap.Tile tile = tileset.getTiles().stream()
                .filter(t -> finalTileId == t.getId())
                .findFirst()
                .orElse(null);

        long tileId = finalTileId;

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
        int tileX = ((int)tileId % tilesPerRow) * tileset.getTileWidth();
        int tileY = ((int)tileId / tilesPerRow) * tileset.getTileHeight();

        return image.getSubimage(tileX, tileY, tileset.getTileWidth(), tileset.getTileHeight());
    }

    private GsonMap.Tileset findTilesetForGid(long gid) {
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
        double camX = this.camera.getX() / this.camera.getZoom();
        double camY = this.camera.getY() / this.camera.getZoom();
        double camW = Config.WINDOW_WIDTH  / this.camera.getZoom();
        double camH = Config.WINDOW_HEIGHT / this.camera.getZoom();
        for (Quad quad : this.staticQuads) {
            if (this.inView(quad, camX, camY, camW, camH)) {
                gr.drawImage(quad.getQuadImage(), (int) quad.getX(), (int) quad.getY(), (int) quad.getWidth(), (int) quad.getHeight(), null);
            }
        }
        drawTool.drawImage(this.batchImage, 0, 0, null);
    }

    public void drawAfterPlayer(DrawTool drawTool) {
        Graphics gr = drawTool.getGraphics2D();
        double camX = this.camera.getX() / this.camera.getZoom();
        double camY = this.camera.getY() / this.camera.getZoom();
        double camW = Config.WINDOW_WIDTH  / this.camera.getZoom();
        double camH = Config.WINDOW_HEIGHT / this.camera.getZoom();
        for (Quad quad : this.staticQuadsAfterPlayer) {
            if (this.inView(quad, camX, camY, camW, camH)) {
                gr.drawImage(quad.getQuadImage(), (int) quad.getX(), (int) quad.getY(), (int) quad.getWidth(), (int) quad.getHeight(), null);
            }
        }
        drawTool.drawImage(this.batchImageAfterPlayer, 0, 0, null);
    }

    private static boolean inView(Quad q, double camX, double camY, double camW, double camH) {
        return q.getX() + q.getWidth()  >= camX - 32 &&
                q.getY() + q.getHeight() >= camY - 32 &&
                q.getX() <= camX + camW + 32 &&
                q.getY() <= camY + camH + 32;
    }

    private static boolean inView(Quad quad) {
        int quadSize = 32;
        CameraRenderer camera = GameScene.getInstance().getCameraRenderer();
        if (quad.getX() >= camera.getX() / camera.getZoom() - quadSize && quad.getY() >= camera.getY() / camera.getZoom() - quadSize) {
            if (quad.getX() + quad.getWidth() <= (camera.getX() + Config.WINDOW_WIDTH) / camera.getZoom() + quadSize &&
                    quad.getY() + quad.getHeight() <= (camera.getY() + Config.WINDOW_HEIGHT) / camera.getZoom() + quadSize) {
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
        private long tileId;
        private GsonMap.Tile tile;
        private long gid;
        private double x;
        private double y;
        private double quadX;
        private double quadY;
        private double width;
        private double height;

        private BufferedImage[] animationFrames;
        private int[] frameDurations;
        private int totalDuration;

        public Quad(Batch batch, long gid, double x, double y, int quadX, int quadY, int width, int height) {
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

            if (this.tile != null && this.tile.getAnimation() != null) {
                List<GsonMap.TileAnimationFrame> frames = this.tile.getAnimation();

                this.animationFrames = new BufferedImage[frames.size()];
                this.frameDurations = new int[frames.size()];

                int tilesPerRow = batch.getTileset().getImageWidth() / batch.getTileset().getTileWidth();
                int tw = batch.getTileset().getTileWidth();
                int th = batch.getTileset().getTileHeight();

                int total = 0;
                for (int i = 0; i < frames.size(); i++) {
                    GsonMap.TileAnimationFrame f = frames.get(i);
                    int tid = (int) f.getTileId();
                    int sx = (tid % tilesPerRow) * tw;
                    int sy = (tid / tilesPerRow) * th;
                    this.animationFrames[i] = batch.getImage().getSubimage(sx, sy, tw, th);
                    this.frameDurations[i]  = f.getDuration();
                    total += f.getDuration();
                }
                this.totalDuration = total;
            }
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
            if (this.animationFrames == null) return this.quadImage;

            int t = (int) ((Wrapper.getTimer().getRunningTime() * 1000) % this.totalDuration);
            int acc = 0;
            int idx = 0;
            for (int i = 0; i < this.frameDurations.length; i++) {
                acc += this.frameDurations[i];
                if (t < acc) {
                    idx = i;
                    break;
                }
            }
            return this.animationFrames[idx];
        }

        public long getGid() {
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
