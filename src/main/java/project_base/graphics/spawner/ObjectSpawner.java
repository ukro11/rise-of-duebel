package project_base.graphics.spawner;

import KAGO_framework.control.Interactable;
import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import project_base.animation.AnimationRenderer;
import project_base.animation.IAnimationState;
import project_base.ProgramController;
import project_base.graphics.IOrderRenderer;
import project_base.model.entity.impl.player.EntityPlayer;
import project_base.model.scene.GameScene;
import project_base.physics.Collider;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ObjectSpawner<T extends Enum<T> & IAnimationState> implements IOrderRenderer, Interactable {

    public static final CopyOnWriteArrayList<ObjectSpawner<?>> objects = new CopyOnWriteArrayList<>();
    private static final HashMap<String, List<Collider>> mapper = new HashMap<>();

    protected ViewController viewController;
    protected ProgramController programController;

    protected ObjectIdResolver id;
    protected Collider collider;
    protected List<Collider> sensorColliders;
    protected AnimationRenderer<T> renderer;
    protected BufferedImage image;

    public ObjectSpawner(ObjectIdResolver id, Collider collider, AnimationRenderer<T> renderer) {
        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();

        this.id = id;
        this.collider = collider;
        this.sensorColliders = new ArrayList<>();
        this.renderer = renderer;
        this.renderer.start();
        if (ObjectSpawner.mapper.containsKey(collider.getId())) {
            this.sensorColliders = ObjectSpawner.mapper.get(collider.getId());
        }
        ObjectSpawner.objects.add(this);
        GameScene.getInstance().getInteractables().add(this);
    }

    public abstract void onRegisterSensor(Collider sensor);
    public abstract void onRegisterPlayer(EntityPlayer player);

    public void update(double dt) {
        if (this.renderer != null) {
            this.renderer.update(dt);
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        if (this.renderer != null) {
            drawTool.drawImage(this.renderer.getCurrentFrame(), this.collider.getX(), this.collider.getY());

        } else {
            drawTool.drawImage(this.image, this.collider.getX(), this.collider.getY());
        }
    }

    @Override
    public double zIndex() {
        return this.collider.getY();
    }

    public ObjectIdResolver getId() { return this.id; }

    public Collider getCollider() {
        return this.collider;
    }

    public List<Collider> getSensorColliders() {
        return this.sensorColliders;
    }

    public void addSensorCollider(Collider sensor) {
        if (!this.sensorColliders.contains(sensor)) this.sensorColliders.add(sensor);
    }

    public void setSensorCollider(List<Collider> sensors) {
        this.sensorColliders = sensors;
    }

    public AnimationRenderer<T> getRenderer() {
        return this.renderer;
    }

    public static ObjectSpawner<?> fetchById(String id) {
        for (ObjectSpawner<?> spawner : ObjectSpawner.objects) {
            if (spawner.getId().getRawId().equals(id)) {
                return spawner;
            }
        }
        return null;
    }

    public static void mapSensor(String id, Collider collider) {
        var list = ObjectSpawner.mapper.getOrDefault(id, new ArrayList<>());
        list.add(collider);
        ObjectSpawner.mapper.put(id, list);
    }
}
