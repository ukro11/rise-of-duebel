package duebel_level.graphics.level;

import KAGO_framework.view.DrawTool;
import duebel_level.Wrapper;
import duebel_level.animation.AnimationRenderer;
import duebel_level.animation.states.PortalAnimationState;
import duebel_level.dyn4j.ColliderBody;
import duebel_level.dyn4j.PhysicsUtils;
import duebel_level.dyn4j.SensorWorldCollider;
import duebel_level.dyn4j.WorldCollider;
import duebel_level.graphics.camera.CameraShake;
import duebel_level.graphics.level.impl.LevelStats;
import duebel_level.graphics.map.GsonMap;
import duebel_level.graphics.map.TileMap;
import duebel_level.model.entity.impl.EntityPlayer;
import duebel_level.model.scene.impl.GameScene;
import duebel_level.model.sound.SoundManager;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.StepListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/***
 * @author Mark
 */
public class LevelMap extends TileMap {

    private static List<String> NEEDED_LAYERS = List.of("SPAWN", "PORTAL", "WORLD", "LOWEST");
    private static List<String> SENSOR_LAYERS = List.of("SPAWN", "PORTAL", "LOWEST", "SENSOR");

    private final LevelLoader loader;
    private List<WorldCollider> sensors;
    private Vector2 spawnLocation;
    private Vector2 portalLocation;

    private AnimationRenderer<PortalAnimationState> portalRenderer;

    private boolean reset = false;

    private StepListenerAdapter<ColliderBody> stepListener;
    private ContactListenerAdapter<ColliderBody> contactListener;

    /**
     * Erstellt eine LevelMap und instanziiert optional einen LevelLoader per Reflection.
     * Prüft außerdem, ob die benötigten Layer in der Map vorhanden sind.
     *
     * @param fileName Map-Datei
     * @param cloader Loader-Klasse (kann null sein)
     * @param staticLayers statische Render-Layer
     * @param staticLayersAfterPlayer statische Render-Layer nach Player
     * @param batchLayers Batch-Render-Layer
     * @param batchLayersAfterPlayer Batch-Render-Layer nach Player
     */
    public LevelMap(String fileName, Class<? extends LevelLoader> cloader, List<String> staticLayers, List<String> staticLayersAfterPlayer, List<String> batchLayers, List<String> batchLayersAfterPlayer) {
        super(fileName, staticLayers, staticLayersAfterPlayer, batchLayers, batchLayersAfterPlayer);
        try {
            this.sensors = new ArrayList<>();

            var m = this.map.getLayers().stream().map(_l -> _l.getName().toUpperCase()).collect(Collectors.toSet());
            if (!this.NEEDED_LAYERS.stream().allMatch(l -> m.contains(l.toUpperCase()))) {
                throw new RuntimeException(String.format("Levels needs these layers: %s, Got these layers: %s", String.join(", ", this.NEEDED_LAYERS), String.join(", ", m)));
            }

            this.portalRenderer = new AnimationRenderer("/graphic/levels/portal.png", 2, 12, 32, 32, PortalAnimationState.IDLE);

            if (cloader != null) {
                this.loader = cloader.getConstructor(LevelMap.class).newInstance(this);

            } else {
                this.loader = new LevelLoader(this.getFileName(), LevelColors.createDefault(), this) {
                    @Override
                    public void updateCollider(TimeStep step, PhysicsWorld<ColliderBody, ?> world) {}
                    @Override
                    public void enterPortal() {}
                    @Override
                    public void resetLevel() {}
                };
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aktiviert das Level: setzt Spieler-Spawn, registriert Listener und fügt Collider zur World hinzu.
     * Behandelt außerdem Death/Reset und Portal-Kollisionen.
     */
    public void onActive() {
        Wrapper.getLocalPlayer().setPosition(this.spawnLocation.x, this.spawnLocation.y);

        WorldCollider lowest = this.getColliderByLayer("LOWEST");
        this.stepListener = new StepListenerAdapter<ColliderBody>() {
            @Override
            public void begin(TimeStep step, PhysicsWorld<ColliderBody, ?> world) {
                super.begin(step, world);
                if (lowest.getY() < Wrapper.getLocalPlayer().getY()) {
                    Wrapper.getLocalPlayer().setPosition(spawnLocation.x, spawnLocation.y);
                    reset = true;
                    Wrapper.getLocalPlayer().getUserProfile().addDeath();
                    Wrapper.getSoundConstants().SOUND_DEATH.setVolume(0.85);
                    SoundManager.playSound(Wrapper.getSoundConstants().SOUND_DEATH, false);
                } else {
                    if (reset) {
                        portalRenderer.switchState(PortalAnimationState.IDLE);
                        GameScene.getInstance().getCameraRenderer().shake(new CameraShake(CameraShake.ShakeType.SMALL_HIT));
                        loader.resetLevel();
                        Wrapper.getLocalPlayer().freeze(0.3);
                        Wrapper.getLocalPlayer().getBody().setLinearVelocity(0, 0);
                        reset = false;
                    }
                }
                loader.updateCollider(step, world);
            }
        };

        Wrapper.getEntityManager().getWorld().addStepListener(this.stepListener);

        this.contactListener = new ContactListenerAdapter<ColliderBody>() {
            @Override
            public void collision(ContactCollisionData<ColliderBody> data) {
                BodyFixture f1 = data.getFixture1();
                BodyFixture f2 = data.getFixture2();
                if (f1.getUserData() == null || f2.getUserData() == null) return;
                if (EntityPlayer.containsPlayer(f1, f2) && PhysicsUtils.contains("PORTAL", f1, f2) &&
                        !Wrapper.getLocalPlayer().isFreeze()) {
                    if (loader instanceof LevelStats) {
                        Wrapper.getLocalPlayer().freeze(0.3);
                        Wrapper.getProgramController().focusDefault(-1);
                        loader.enterPortal();
                        Wrapper.getLevelManager().nextLevel(String.format("PORTAL-%d", Wrapper.getLevelManager().getIndex()));

                    } else {
                        Wrapper.getLocalPlayer().freeze(0.6);
                        portalRenderer.switchState(PortalAnimationState.DISAPPEAR);
                        Wrapper.getProgramController().focusPlayer(-1);
                        Wrapper.getLocalPlayer().setVisible(false);
                        Wrapper.getLocalPlayer().getBody().setLinearVelocity(0, 0);
                        portalRenderer.onFinish(PortalAnimationState.DISAPPEAR, () -> {
                            loader.enterPortal();
                            Wrapper.getLevelManager().nextLevel(String.format("PORTAL-%d", Wrapper.getLevelManager().getIndex()), () -> {
                                portalRenderer.switchState(PortalAnimationState.IDLE);
                                Wrapper.getLocalPlayer().setVisible(true);
                                Wrapper.getProgramController().focusDefault(-1);
                            });
                        });
                    }
                }
            }
        };

        Wrapper.getEntityManager().getWorld().addContactListener(this.contactListener);
        if (this.loader != null) this.loader.onActive();
        this.colliders.forEach(c -> {
            if (this.loader != null) {
                this.loader.loadCollider(c, c.getFixture());
            }
            Wrapper.getEntityManager().getWorld().addBody(c);
        });
    }

    /**
     * Deaktiviert das Level: entfernt Listener und löscht alle Collider aus der World.
     */
    public void onHide() {
        Wrapper.getEntityManager().getWorld().removeStepListener(this.stepListener);
        Wrapper.getEntityManager().getWorld().removeContactListener(this.contactListener);
        this.removeAll();
    }

    /** Entfernt alle Collider aus der Physics-World. */
    private void removeAll() {
        colliders.forEach(c -> Wrapper.getEntityManager().getWorld().removeBody(c));
    }

    /**
     * Erzeugt WorldCollider/SensorWorldCollider aus Map-Objekten und setzt Filter/Sensoren.
     * Aktualisiert außerdem Spawn- und Portal-Position.
     *
     * @param layer Map-Layer
     * @param o Collider-Objekt aus der Map
     */
    @Override
    public void loadCollider(GsonMap.Layer layer, GsonMap.ObjectCollider o) {
        WorldCollider wc = null;
        if (layer.getName().equalsIgnoreCase("SENSOR")) {
            wc = new SensorWorldCollider(o, new ObjectIdResolver(o.getName()));

        } else {
            wc = new WorldCollider(o, new ObjectIdResolver(o.getName()));
        }

        wc.setLayer(layer.getName());

        var zindex = o.getProperty("zindex");
        wc.setZIndex(zindex == null ? 0 : ((Double) zindex.getValue()).intValue());

        BodyFixture fix = null;

        if (o.isEllipse()) {
            fix = wc.addFixture(Geometry.createEllipse(o.getWidth(), o.getHeight()));
            wc.translate(o.getX(), o.getY());

        } else if (o.getPolygon() != null) {
            Vector2[] vertices = new Vector2[o.getPolygon().size()];
            for (int i = 0; i < vertices.length; i++) {
                var v = o.getPolygon().get(i);
                vertices[i] = new Vector2(v.getX(), v.getY());
            }
            fix = wc.addFixture(Geometry.createPolygon(vertices));

        } else {
            var vertices = new Vector2[] {
                    new Vector2(o.getX(), o.getY()),
                    new Vector2(o.getX() + o.getWidth(), o.getY()),
                    new Vector2(o.getX() + o.getWidth(), o.getY() + o.getHeight()),
                    new Vector2(o.getX(), o.getY() + o.getHeight()),
            };
            fix = wc.addFixture(Geometry.createPolygon(vertices));
        }

        if (layer.getName().equalsIgnoreCase("SPAWN")) {
            this.spawnLocation = new Vector2(o.getX(), o.getY());
        }

        if (layer.getName().equals("PORTAL")) {
            this.portalLocation = new Vector2(o.getX(), o.getY());
        }

        if (layer.getObjects().size() == 1 && wc.getUserData().isEmpty()) {
            wc.setUserData(layer.getName().toUpperCase());
        }

        if (layer.getName().equals("MOVING")) {
            fix.setFilter(new CategoryFilter(ColliderBody.MASK_PLATFORM_MOVING, ColliderBody.FILTER_MOVING_PLATFORM));

        } else if (layer.getName().equals("WORLD")) {
            fix.setFilter(new CategoryFilter(ColliderBody.MASK_PLATFORM, ColliderBody.FILTER_PLATFORM));
        }

        fix.setUserData(wc.getUserData());

        if (LevelMap.SENSOR_LAYERS.contains(layer.getName())) {
            fix.setSensor(true);
            WorldCollider collider = this.getColliderBySensor(wc);
            if (collider != null) {
                collider.getSensors().add(wc);
                collider.getSensors().sort(Comparator.comparing(s -> s.getResolver().getIndex()));
            }
        }

        this.colliders.add(wc);
    }

    /**
     * Aktualisiert Portal-Animation (außer im Stats-Level).
     *
     * @param dt delta time
     */
    public void update(double dt) {
        if (this.portalLocation != null && !this.getFileName().equals("stats.json")) {
            if (!this.portalRenderer.isRunning()) this.portalRenderer.start();
            this.portalRenderer.update(dt);
        }
    }

    /**
     * Zeichnet Debug-Collider mit Prefix "D$" über den Loader.
     *
     * @param drawTool DrawTool
     */
    private void drawCollider(DrawTool drawTool) {
        this.colliders.forEach(c -> {
            if (c.getUserData().startsWith("D$")) {
                this.loader.drawCollider(c, drawTool);
            }
        });
    }

    /**
     * Zeichnet Debug-Collider mit Prefix "DA$" nach dem Player über den Loader.
     *
     * @param drawTool DrawTool
     */
    private void drawColliderAfter(DrawTool drawTool) {
        this.colliders.forEach(c -> {
            if (c.getUserData().startsWith("DA$")) {
                this.loader.drawCollider(c, drawTool);
            }
        });
    }

    /**
     * Zeichnet TileMap, Debug-Collider, Loader-Inhalte und Portal.
     *
     * @param drawTool DrawTool
     */
    @Override
    public void draw(DrawTool drawTool) {
        if (!this.staticQuads.isEmpty()) super.draw(drawTool);
        this.drawCollider(drawTool);
        if (this.loader != null) {
            this.loader.draw(drawTool);
        }
        if (this.portalLocation != null && !this.getFileName().equals("stats.json")) {
            drawTool.push();
            drawTool.getGraphics2D().drawImage(
                    this.portalRenderer.getCurrentFrame(),
                    (int) this.portalLocation.x - 24,
                    (int) this.portalLocation.y,
                    64,
                    64,
                    null
            );
            drawTool.pop();
        }
    }

    /**
     * Zeichnet Layer nach dem Player und entsprechende Debug-Collider.
     *
     * @param drawTool DrawTool
     */
    @Override
    public void drawAfterPlayer(DrawTool drawTool) {
        if (!this.staticQuadsAfterPlayer.isEmpty()) super.drawAfterPlayer(drawTool);
        this.drawColliderAfter(drawTool);
    }

    /**
     * Liefert alle Sensoren, die zu einem Collider gehören (über Resolver-Format).
     *
     * @param collider Haupt-Collider
     * @return Sensoren-Liste
     */
    public List<WorldCollider> getSensorsByCollider(WorldCollider collider) {
        return this.sensors.stream().filter(s -> {
            String[] sp = s.getResolver().getType().split("-");
            if (sp.length < 3 || !s.getResolver().isValid()) return false;

            String layer = sp[0];
            String type = sp[1];
            String index = sp[2];
            return collider.getResolver().getLayer().equals(layer) && collider.getResolver().getType().equals(type) && collider.getResolver().getIndex() == Integer.parseInt(index);

        }).collect(Collectors.toList());
    }

    /**
     * Findet den Haupt-Collider zu einem Sensor (über Resolver-Format).
     *
     * @param sensor Sensor-Collider
     * @return zugehöriger Collider oder null
     */
    public WorldCollider getColliderBySensor(WorldCollider sensor) {
        if (!sensor.getResolver().isValid()) return null;
        return this.colliders.stream().filter(c -> {
            String[] sp = sensor.getResolver().getType().split("-");
            if (sp.length < 3 || !c.getResolver().isValid()) return false;

            String layer = sp[0];
            String type = sp[1];
            String index = sp[2];
            return c.getResolver().getLayer().equals(layer) && c.getResolver().getType().equals(type) && c.getResolver().getIndex() == Integer.parseInt(index);

        }).findAny().orElse(null);
    }

    /**
     * @param collider Haupt-Collider
     * @return erster zugehöriger Sensor
     */
    public WorldCollider getSensorByCollider(WorldCollider collider) {
        return this.getSensorsByCollider(collider).get(0);
    }

    /**
     * @param collider Haupt-Collider
     * @param index Sensor-Index in der Liste
     * @return Sensor
     */
    public WorldCollider getSensorByCollider(WorldCollider collider, int index) {
        return this.getSensorsByCollider(collider).get(index);
    }

    /**
     * Sucht einen Collider per Suffix im UserData.
     *
     * @param name Name/Suffix
     * @return Collider oder null
     */
    public WorldCollider getColliderByName(String name) {
        return this.colliders.stream().filter(c -> ((String) c.getUserData()).endsWith(name)).findFirst().orElse(null);
    }

    /**
     * Liefert Collider eines Layers, sortiert nach Resolver-Index.
     *
     * @param layer Layername
     * @return Collider-Liste
     */
    public List<WorldCollider> getCollidersByLayer(String layer) {
        return this.colliders.stream().filter(c -> c.getLayer().equals(layer)).sorted(Comparator.comparing(c -> c.getResolver() != null ? c.getResolver().getIndex() : 0)).collect(Collectors.toList());
    }

    /**
     * @param layer Layername
     * @return erster Collider im Layer
     */
    public WorldCollider getColliderByLayer(String layer) {
        return this.getCollidersByLayer(layer).get(0);
    }

    /**
     * @param layer Layername
     * @param index Index innerhalb des Layers
     * @return Collider
     */
    public WorldCollider getColliderByLayer(String layer, int index) {
        return this.getCollidersByLayer(layer).get(index);
    }

    /**
     * @return zugehöriger LevelLoader
     */
    public LevelLoader getLoader() {
        return this.loader;
    }
}
