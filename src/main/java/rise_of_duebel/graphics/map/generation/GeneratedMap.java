package rise_of_duebel.graphics.map.generation;

import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.Config;
import rise_of_duebel.Wrapper;
import rise_of_duebel.event.events.CameraMoveEvent;
import rise_of_duebel.event.services.process.EventPostGameLoadingProcess;
import rise_of_duebel.graphics.sprite.SpritesheetRenderer;
import rise_of_duebel.graphics.sprite.states.TerrainStates;
import rise_of_duebel.model.scene.GameScene;
import rise_of_duebel.utils.MathUtils;

import java.awt.*;
import java.util.HashMap;

public class GeneratedMap {

    private static final Logger log = LoggerFactory.getLogger(GeneratedMap.class);
    private final Noise terrainNoise;
    private final Noise treeNoise;

    private final SpritesheetRenderer<TerrainStates> renderer;
    private final HashMap<TileCoord, TileInfo<TerrainStates, TileType>> tilesetCache = new HashMap<>();
    private final int TILE_SIZE = 16;
    private final double NOISE_COORD_SCALE = 0.05; // 0.1
    private final double GROUND_THRESHOLD = 0.55;

    public GeneratedMap() {
        this.terrainNoise = new Noise();
        this.treeNoise = new Noise();
        // 11 x 7
        this.renderer = new SpritesheetRenderer<>("/graphic/temp/undead/Tiled_files/water_coasts.png", 7, 11, TerrainStates.class);

        TileCoord coord = new TileCoord(0, 0);
        boolean found = false;
        while (!found) {
            if (this.getTerrainNoise(coord) < this.GROUND_THRESHOLD) {
                coord = coord.right().below();

            } else {
                found = true;
                TileCoord finalCoord = coord;
                Wrapper.getProcessManager().queue(new EventPostGameLoadingProcess<>("Spawn player", () -> {
                    log.info("{}", finalCoord);
                    //Wrapper.getLocalPlayer().getBody().setPosition(finalCoord.x() / this.NOISE_COORD_SCALE, finalCoord.y() / this.NOISE_COORD_SCALE);

                }));
            }
        }

        Wrapper.getEventManager().addEventListener("cameraMove", (CameraMoveEvent event) -> {
            this.loadChunks();
        });
    }

    private TileInfo<TerrainStates, TileType> getTile(TileCoord c) {
        //if (this.visibleChunk(c.x(), c.y())) {
            //return this.tilesetCache.getOrDefault(c, this.computeNoise(c));
        //}
        return this.tilesetCache.get(c);
    }

    private TileInfo<TerrainStates, TileType> getTile(double x, double y) {
        return this.getTile(new TileCoord(x, y));
    }

    private double getTerrainNoise(TileCoord c) {
        return this.terrainNoise.normalizedNoise(Math.floor(c.x()) * this.NOISE_COORD_SCALE, Math.floor(c.y()) * this.NOISE_COORD_SCALE);
    }

    private double getTerrainNoise(double x, double y) {
        return this.terrainNoise.normalizedNoise(Math.floor(x) * this.NOISE_COORD_SCALE, Math.floor(y) * this.NOISE_COORD_SCALE);
    }

    private void loadChunks() {
        double startX = GameScene.getInstance().getCameraRenderer().getWorldX() - this.TILE_SIZE;
        double startY = GameScene.getInstance().getCameraRenderer().getWorldY() - this.TILE_SIZE;
        int chunks = 3;

        for (int i = -chunks; i < Math.ceil(Config.WINDOW_WIDTH / this.TILE_SIZE) + chunks; i++) {
            for (int j = -chunks; j < Math.ceil(Config.WINDOW_HEIGHT / this.TILE_SIZE) + chunks; j++) {
                TileCoord coord = new TileCoord(Math.floor(startX / this.TILE_SIZE) + i, Math.floor(startY / this.TILE_SIZE) + j);
                //double tree = this.treeNoise.noise(coord.x(), coord.y());

                //TerrainStates state = TerrainStates.getRandomGrass();




                this.tilesetCache.putIfAbsent(coord, this.computeNoise(coord));
                    /*
                    double h = noiseHeight / maxAmplitude;
                    heightMap[x][y] = h;
                    */
            }
        }
    }

    private TileInfo<TerrainStates, TileType> computeNoise(TileCoord coord) {
        double terrain = this.getTerrainNoise(coord);

        TerrainStates state = null;
        TileType type = TileType.OTHER;



        if (terrain < 0.25) {
            // tiefes, kaltes Wasser

        } else if (terrain < this.GROUND_THRESHOLD) {
            // sumpfig-bläulich/grün

        } else {
            type = TileType.GRASS;
            // Grün

            // top
            if (this.getTerrainNoise(coord.above()) < this.GROUND_THRESHOLD) {
                if (this.getTerrainNoise(coord.left()) < this.GROUND_THRESHOLD) {
                    state = TerrainStates.GRASS_CORNER_LT_2;

                    // right-top
                } else if (this.getTerrainNoise(coord.right()) < this.GROUND_THRESHOLD) {
                    state = TerrainStates.GRASS_CORNER_RT_2;

                    // top
                } else {
                    state = TerrainStates.GRASS_CORNER_T_2;
                }

            } else {
                if (this.getTerrainNoise(coord.left()) < this.GROUND_THRESHOLD) {
                    state = TerrainStates.GRASS_CORNER_L;

                } else if (this.getTerrainNoise(coord.right()) < this.GROUND_THRESHOLD) {
                    state = TerrainStates.GRASS_CORNER_R;

                } else {
                    state = TerrainStates.getRandomGrass();
                }
            }
        }
        return new TileInfo<>(state, type, coord, terrain, 0);
    }

    public void update(double dt) {}

    public void draw(DrawTool drawTool) {
        for (int i = 0; i < Math.ceil(Config.WINDOW_WIDTH / this.TILE_SIZE); i++) {
            for (int j = 0; j < Math.ceil(Config.WINDOW_HEIGHT / this.TILE_SIZE); j++) {
                double startX = GameScene.getInstance().getCameraRenderer().getWorldX() - this.TILE_SIZE;
                double startY = GameScene.getInstance().getCameraRenderer().getWorldY() - this.TILE_SIZE;
                if (this.visibleChunk(startX + i * this.TILE_SIZE, startY + j * this.TILE_SIZE)) {
                    int relX = (int) Math.floor(startX / this.TILE_SIZE);
                    int relY = (int) Math.floor(startY / this.TILE_SIZE);
                    TileCoord coord = new TileCoord(relX + i, relY + j);
                    TileInfo<TerrainStates, TileType> info = this.tilesetCache.get(coord);

                    if (info == null || info.state() == null) {
                        //drawTool.push();
                        //drawTool.setCurrentColor(new Color(208, 185, 156));
                        //drawTool.drawFilledRectangle((relX + i) * this.TILE_SIZE, (relY + j) * this.TILE_SIZE, this.TILE_SIZE, this.TILE_SIZE);
                        //drawTool.pop();

                    } else {
                        //drawTool.setCurrentColor(Color.white);
                        drawTool.push();
                        switch (info.state()) {
                            case GRASS_CORNER_LT_2 -> {
                                this.renderer.renderSprite(TerrainStates.GRASS_CORNER_LT_1,(relX + i) * this.TILE_SIZE, (relY + j - 1) * this.TILE_SIZE, drawTool);
                            }
                            case GRASS_CORNER_T_2 -> {
                                this.renderer.renderSprite(TerrainStates.GRASS_CORNER_T_1,(relX + i) * this.TILE_SIZE, (relY + j - 1) * this.TILE_SIZE, drawTool);
                            }
                            case GRASS_CORNER_RT_2 -> {
                                this.renderer.renderSprite(TerrainStates.GRASS_CORNER_RT_1,(relX + i) * this.TILE_SIZE, (relY + j - 1) * this.TILE_SIZE, drawTool);
                            }
                        }

                        this.renderer.renderSprite(info.state(),(relX + i) * this.TILE_SIZE, (relY + j) * this.TILE_SIZE, drawTool);
                        drawTool.pop();
                    }
                }
            }
        }
    }

    private boolean visibleChunk(double x, double y) {
        double chunk = this.TILE_SIZE * 3;
        if (x >= GameScene.getInstance().getCameraRenderer().getX() / GameScene.getInstance().getCameraRenderer().getZoom() - this.TILE_SIZE - chunk && y >= GameScene.getInstance().getCameraRenderer().getY() / GameScene.getInstance().getCameraRenderer().getZoom() - this.TILE_SIZE - chunk * 2) {
            if (x + this.TILE_SIZE <= (GameScene.getInstance().getCameraRenderer().getX() + Config.WINDOW_WIDTH) / GameScene.getInstance().getCameraRenderer().getZoom() + this.TILE_SIZE + chunk &&
                    y + this.TILE_SIZE <= (GameScene.getInstance().getCameraRenderer().getY() + Config.WINDOW_HEIGHT) / GameScene.getInstance().getCameraRenderer().getZoom() + (this.TILE_SIZE + chunk) * 2) {
                return true;
            }
        }
        return false;
    }

    private boolean inView(double x, double y) {
        if (x >= GameScene.getInstance().getCameraRenderer().getX() / GameScene.getInstance().getCameraRenderer().getZoom() - this.TILE_SIZE && y >= GameScene.getInstance().getCameraRenderer().getY() / GameScene.getInstance().getCameraRenderer().getZoom() - this.TILE_SIZE) {
            if (x + this.TILE_SIZE <= (GameScene.getInstance().getCameraRenderer().getX() + Config.WINDOW_WIDTH) / GameScene.getInstance().getCameraRenderer().getZoom() + this.TILE_SIZE &&
                    y + this.TILE_SIZE <= (GameScene.getInstance().getCameraRenderer().getY() + Config.WINDOW_HEIGHT) / GameScene.getInstance().getCameraRenderer().getZoom() + this.TILE_SIZE * 2) {
                return true;
            }
        }
        return false;
    }

    private Color lerpColor(Color a, Color b, double t) {
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl= (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(clamp255(r), clamp255(g), clamp255(bl));
    }

    private int clamp255(int v) { return Math.max(0, Math.min(255, v)); }

    private Color hex(int rgb) { return new Color((rgb>>16)&255, (rgb>>8)&255, rgb&255); }

    private double smoothstep(double a, double b, double x) {
        x = MathUtils.clamp((x - a) / (b - a), 0.0, 1.0);
        return x * x * (3 - 2 * x);
    }

    private int fastHash(int x) {
        x ^= (x << 13);
        x ^= (x >>> 17);
        x ^= (x << 5);
        return x;
    }

    private double hashToUnit(int h) {
        // map signed int to [0,1]
        return (h - (double)Integer.MIN_VALUE) / (double)0xFFFFFFFFL;
    }

    public void drawAfterPlayer(DrawTool drawTool) {

    }
}
