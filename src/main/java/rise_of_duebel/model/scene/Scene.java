package rise_of_duebel.model.scene;

import KAGO_framework.control.ViewController;
import KAGO_framework.model.abitur.datenstrukturen.Queue;
import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.ProgramController;
import rise_of_duebel.graphics.gui.Gui;
import rise_of_duebel.model.debug.VisualModel;
import rise_of_duebel.model.scene.impl.GameScene;
import rise_of_duebel.model.transitions.Transition;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public abstract class Scene {

    private static final Logger log = LoggerFactory.getLogger(Scene.class);
    private static HashMap<String, Scene> scenes = new HashMap<>();

    private static Queue<SceneSwitch> sceneSwitchQueue;
    private static SceneSwitch transition;

    private static Scene last;
    private static Scene current;
    protected Gui currentGui;
    protected ViewController viewController;
    protected ProgramController programController;

    private String id;
    protected CopyOnWriteArrayList<VisualModel> visuals;

    public Scene(String id) {
        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();
        this.id = id;
        this.visuals = new CopyOnWriteArrayList<>();
        Scene.sceneSwitchQueue = new Queue<>();
        Scene.scenes.put(this.id, this);
    }

    public static void showGameScene() {
        GameScene next = GameScene.getInstance();
        if (Scene.current != null) {
            Scene.current.onClose(next);
        }
        next.onOpen(Scene.last);
        Scene.current = next;
    }

    public static void open(Scene scene, Transition sceneTransition) {
        if (scene == null) return;

        SceneSwitch sceneSwitch = new SceneSwitch(Scene.current, scene, sceneTransition);
        if (Scene.transition == null) {
            Scene.transition = sceneSwitch;
            sceneTransition.in(Scene.current);

        } else if (!Scene.transition.next().equals(scene)) {
            Scene.sceneSwitchQueue.enqueue(sceneSwitch);
        }
    }

    public void openGUI(Gui gui) {
        if (this.currentGui != null) this.currentGui.onGuiClose();
        this.currentGui = gui;
        if (this.currentGui != null) this.currentGui.onGuiOpen();
    }

    public void closeGUI() {
        this.currentGui = null;
    }

    public static void updateAll(double dt) {
        if (Scene.transition != null) {
            Scene last = Scene.transition.last();
            Scene next = Scene.transition.next();
            Transition tr = Scene.transition.transition();

            if (tr.swap()) {
                if (Scene.current != next) {
                    if (Scene.current != null) {
                        Scene.current.onClose(next);
                    }
                    next.onOpen(Scene.last);
                    Scene.current = next;
                    tr.out(last, next);
                }
                if (tr.finished()) {
                    Scene.transition = null;
                }
            }
        } else {
            if (!Scene.sceneSwitchQueue.isEmpty()) {
                Scene.transition = Scene.sceneSwitchQueue.front();
                Scene.sceneSwitchQueue.dequeue();
            }
        }
    }

    public static void drawTransition(DrawTool drawTool) {
        if (Scene.transition != null) {
            Scene.transition.transition().draw(drawTool);
        }
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

    public VisualModel getVisual(String id) {
        return this.visuals.stream().filter(_vis -> _vis.getId().equals(id)).findFirst().orElse(null);
    }

    public void draw(DrawTool drawTool) {
        for (VisualModel visual : this.visuals) {
            if (visual.isVisible()) {
                visual.draw(drawTool);
            }
        }
        if (this.currentGui != null) this.currentGui.draw(drawTool);
    }

    public void update(double dt) {
        for (VisualModel visual : this.visuals) {
            visual.update(dt);
        }
        if (this.currentGui != null) this.currentGui.update(dt);
    }

    public void onOpen(Scene last) {}
    public void onClose(Scene newScene) {}

    public void mouseEntered(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseReleased(e);
    }

    public void mouseClicked(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseClicked(e);
    }

    public void mouseDragged(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseDragged(e);
    }

    public void mouseMoved(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseMoved(e);
    }

    public void mousePressed(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mousePressed(e);
    }

    public void keyTyped(KeyEvent e) {
        if (this.currentGui != null) this.currentGui.keyTyped(e);
    }

    public void keyPressed(KeyEvent e) {
        if (this.currentGui == null) {
            List<Gui> guisOpen = Gui.guis.stream().filter(it -> it.keyToOpen() == e.getKeyCode() && it.shouldOpen()).collect(Collectors.toList());
            if (guisOpen.size() > 1) log.warn("GUI Open Event has found two guis with same conditions but opened only one");
            if (guisOpen.size() > 0) {
                this.currentGui = guisOpen.get(0);
                this.currentGui.onGuiOpen();
            }

        } else {
            this.currentGui.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                this.currentGui.onGuiClose();
                this.currentGui = this.currentGui.getParent();
                if (this.currentGui != null) this.currentGui.onGuiOpen();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (this.currentGui != null) this.currentGui.keyReleased(e);
    }
}
