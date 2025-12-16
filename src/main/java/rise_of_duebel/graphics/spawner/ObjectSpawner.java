package rise_of_duebel.graphics.spawner;

import KAGO_framework.control.Interactable;
import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import rise_of_duebel.ProgramController;
import rise_of_duebel.animation.AnimationRenderer;
import rise_of_duebel.animation.IAnimationState;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.graphics.IOrderRenderer;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.scene.GameScene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ObjectSpawner<T extends Enum<T> & IAnimationState> implements IOrderRenderer, Interactable {

    public static final CopyOnWriteArrayList<ObjectSpawner<?>> objects = new CopyOnWriteArrayList<>();
    private static final HashMap<String, List<ColliderBody>> mapper = new HashMap<>();

    protected ViewController viewController;
    protected ProgramController programController;

    protected ObjectIdResolver id;
    protected ColliderBody collider;
    protected List<ColliderBody> sensorColliders;
    protected AnimationRenderer<T> renderer;
    protected BufferedImage image;

    public ObjectSpawner(ObjectIdResolver id, ColliderBody collider, AnimationRenderer<T> renderer) {
        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();

        this.id = id;
        this.collider = collider;
        this.sensorColliders = new ArrayList<>();
        this.renderer = renderer;
        this.renderer.start();
        if (ObjectSpawner.mapper.containsKey(collider.getUserData())) {
            this.sensorColliders = ObjectSpawner.mapper.get(collider.getUserData());
        }
        ObjectSpawner.objects.add(this);
        GameScene.getInstance().getInteractables().add(this);
    }

    public abstract void onRegisterSensor(ColliderBody sensor);
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

    public ColliderBody getCollider() {
        return this.collider;
    }

    public List<ColliderBody> getSensorColliders() {
        return this.sensorColliders;
    }

    public void addSensorCollider(ColliderBody sensor) {
        if (!this.sensorColliders.contains(sensor)) this.sensorColliders.add(sensor);
    }

    public void setSensorCollider(List<ColliderBody> sensors) {
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

    public static void mapSensor(String id, ColliderBody collider) {
        var list = ObjectSpawner.mapper.getOrDefault(id, new ArrayList<>());
        list.add(collider);
        ObjectSpawner.mapper.put(id, list);
    }
}
