package rise_of_duebel.model.scene;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import rise_of_duebel.ProgramController;
import rise_of_duebel.model.debug.VisualModel;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Scene {

    private static HashMap<String, Scene> scenes = new HashMap<>();
    private static Scene last;
    private static Scene current;
    protected ViewController viewController;
    protected ProgramController programController;

    private String id;
    protected CopyOnWriteArrayList<VisualModel> visuals;

    public Scene(String id) {
        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();
        this.id = id;
        this.visuals = new CopyOnWriteArrayList<>();
        Scene.scenes.put(this.id, this);
    }

    public static void open(Scene scene) {
        if (Scene.current != null) {
            Scene.current.onClose(scene);
        }
        scene.onOpen(Scene.last);
        Scene.current = scene;
    }

    public static void close() {
        Scene.current.onClose(null);
        Scene.current = GameScene.getInstance();
    }

    public static HashMap<String, Scene> getScenes() {
        return Scene.scenes;
    }

    public static Scene getCurrentScene() {
        return Scene.current;
    }

    public String getId() {
        return this.id;
    }

    public List<VisualModel> getVisuals() {
        return this.visuals;
    }

    public void draw(DrawTool drawTool) {
        for (VisualModel visual : this.visuals) {
            visual.draw(drawTool);
        }
    }

    public void update(double dt) {
        for (VisualModel visual : this.visuals) {
            visual.update(dt);
        }
    }

    public void onOpen(Scene last) {}
    public void onClose(Scene newScene) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}
}
