package rise_of_duebel.graphics.level;

import KAGO_framework.view.DrawTool;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.StepListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.Wrapper;
import rise_of_duebel.animation.AnimationRenderer;
import rise_of_duebel.animation.states.PortalAnimationState;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.dyn4j.PhysicsUtils;
import rise_of_duebel.dyn4j.SensorWorldCollider;
import rise_of_duebel.dyn4j.WorldCollider;
import rise_of_duebel.graphics.camera.CameraShake;
import rise_of_duebel.graphics.level.impl.LevelStats;
import rise_of_duebel.graphics.map.GsonMap;
import rise_of_duebel.graphics.map.TileMap;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.scene.impl.GameScene;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LevelMap extends TileMap {

    private static final Logger log = LoggerFactory.getLogger(LevelMap.class);
    private static List<String> NEEDED_LAYERS = List.of("SPAWN", "PORTAL", "WORLD", "LOWEST");
    private static List<String> SENSOR_LAYERS = List.of("SPAWN", "PORTAL", "LOWEST", "SENSOR");

    private final LevelLoader loader;
    private List<WorldCollider> sensors;
    private Vector2 spawnLocation;
    private Vector2 portalLocation;

    private AnimationRenderer<PortalAnimationState> portalRenderer;

    private boolean reset = false;
    private double portalCooldownSeconds = 0.0;

    private StepListenerAdapter<ColliderBody> stepListener;
    private ContactListenerAdapter<ColliderBody> contactListener;

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
    public void onActive() {
        Wrapper.getLocalPlayer().setPosition(this.spawnLocation.x, this.spawnLocation.y);

        this.portalCooldownSeconds = 4.0;

        WorldCollider lowest = this.getColliderByLayer("LOWEST");
        this.stepListener = new StepListenerAdapter<ColliderBody>() {
            @Override
            public void begin(TimeStep step, PhysicsWorld<ColliderBody, ?> world) {
                super.begin(step, world);
                if (lowest.getY() < Wrapper.getLocalPlayer().getY()) {
                    Wrapper.getLocalPlayer().setPosition(spawnLocation.x, spawnLocation.y);
                    reset = true;
                    Wrapper.getUserProfile().addDeath();
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
                if (portalCooldownSeconds > 0.0) return;
                if (EntityPlayer.containsPlayer(f1, f2) && PhysicsUtils.contains("PORTAL", f1, f2) &&
                        !Wrapper.getLocalPlayer().isFreeze()) {
                    if (loader instanceof LevelStats) {
                        Wrapper.getLocalPlayer().freeze(0.3);
                        Wrapper.getProgramController().focusDefault(-1);
                        loader.enterPortal();
                        Wrapper.getLevelManager().nextLevel(String.format("PORTAL-%d", Wrapper.getLevelManager().getIndex()));

                    } else if (portalCooldownSeconds == 0) {
                        Wrapper.getLocalPlayer().freeze(1.0);
                        portalRenderer.switchState(PortalAnimationState.DISAPPEAR);
                        Wrapper.getProgramController().focusPlayer(-1);
                        Wrapper.getLocalPlayer().setVisible(false);
                        portalRenderer.onFinish(() -> {
                            loader.enterPortal();
                            portalRenderer.switchState(PortalAnimationState.IDLE);
                            Wrapper.getLevelManager().nextLevel(String.format("PORTAL-%d", Wrapper.getLevelManager().getIndex()), () -> {
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

    public void onHide() {
        Wrapper.getEntityManager().getWorld().removeStepListener(this.stepListener);
        Wrapper.getEntityManager().getWorld().removeContactListener(this.contactListener);
        this.removeAll();
    }

    private void removeAll() {
        colliders.forEach(c -> Wrapper.getEntityManager().getWorld().removeBody(c));
    }

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

    public void update(double dt) {
        if (this.portalCooldownSeconds > 0.0) {
            this.portalCooldownSeconds -= dt;
            if (this.portalCooldownSeconds < 0.0) this.portalCooldownSeconds = 0.0;
        }

        if (this.portalLocation != null && !this.getFileName().equals("stats.json")) {
            if (!this.portalRenderer.isRunning()) this.portalRenderer.start();
            this.portalRenderer.update(dt);
        }
    }

    private void drawCollider(DrawTool drawTool) {
        this.colliders.forEach(c -> {
            if (c.getUserData().startsWith("D$")) {
                this.loader.drawCollider(c, drawTool);
            }
        });
    }

    private void drawColliderAfter(DrawTool drawTool) {
        this.colliders.forEach(c -> {
            if (c.getUserData().startsWith("DA$")) {
                this.loader.drawCollider(c, drawTool);
            }
        });
    }

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
                    (int) this.portalLocation.x - 32,
                    (int) this.portalLocation.y,
                    64,
                    64,
                    null
            );
            drawTool.pop();
        }
    }

    @Override
    public void drawAfterPlayer(DrawTool drawTool) {
        if (!this.staticQuadsAfterPlayer.isEmpty()) super.drawAfterPlayer(drawTool);
        this.drawColliderAfter(drawTool);
    }

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

    public WorldCollider getSensorByCollider(WorldCollider collider) {
        return this.getSensorsByCollider(collider).get(0);
    }

    public WorldCollider getSensorByCollider(WorldCollider collider, int index) {
        return this.getSensorsByCollider(collider).get(index);
    }

    public WorldCollider getColliderByName(String name) {
        return this.colliders.stream().filter(c -> ((String) c.getUserData()).endsWith(name)).findFirst().orElse(null);
    }

    public List<WorldCollider> getCollidersByLayer(String layer) {
        return this.colliders.stream().filter(c -> c.getLayer().equals(layer)).sorted(Comparator.comparing(c -> c.getResolver() != null ? c.getResolver().getIndex() : 0)).collect(Collectors.toList());
    }

    public WorldCollider getColliderByLayer(String layer) {
        return this.getCollidersByLayer(layer).get(0);
    }

    public WorldCollider getColliderByLayer(String layer, int index) {
        return this.getCollidersByLayer(layer).get(index);
    }

    public LevelLoader getLoader() {
        return this.loader;
    }
}
