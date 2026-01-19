package duebel_level.model.scene;

import KAGO_framework.control.ViewController;
import KAGO_framework.model.abitur.datenstrukturen.Queue;
import KAGO_framework.view.DrawTool;
import duebel_level.ProgramController;
import duebel_level.graphics.gui.Gui;
import duebel_level.model.debug.VisualModel;
import duebel_level.model.scene.impl.GameScene;
import duebel_level.model.transitions.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/***
 * @author Mark
 */
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

    /**
     * Erstellt eine Szene, registriert sie unter ihrer ID und initialisiert die Visual-Liste.
     *
     * @param id eindeutige Szenen-ID
     */
    public Scene(String id) {
        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();
        this.id = id;
        this.visuals = new CopyOnWriteArrayList<>();
        Scene.sceneSwitchQueue = new Queue<>();
        Scene.scenes.put(this.id, this);
    }

    /**
     * Öffnet die GameScene sofort (ohne Queue-Logik).
     * Ruft onClose/onOpen passend auf.
     */
    public static void showGameScene() {
        GameScene next = GameScene.getInstance();
        if (Scene.current != null) {
            Scene.current.onClose(next);
        }
        next.onOpen(Scene.last);
        Scene.current = next;
    }

    /**
     * Startet einen Szenenwechsel mit Transition.
     * Falls bereits ein Wechsel läuft, wird der Wechsel ggf. in die Queue gelegt.
     *
     * @param scene Zielszene
     * @param sceneTransition Transition-Implementierung für den Wechsel
     */
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

    /**
     * Öffnet eine GUI in dieser Szene und schließt die bisherige GUI.
     *
     * @param gui neue GUI (oder null)
     */
    public void openGUI(Gui gui) {
        if (this.currentGui != null) this.currentGui.onGuiClose();
        this.currentGui = gui;
        if (this.currentGui != null) this.currentGui.onGuiOpen();
    }

    /** Schließt die aktuelle GUI ohne Callback. */
    public void closeGUI() {
        this.currentGui = null;
    }

    /**
     * Aktualisiert den globalen Szenenwechsel-Status (Transition/Queue).
     *
     * @param dt delta time
     */
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

    /**
     * Zeichnet die aktuelle Transition (falls aktiv).
     *
     * @param drawTool DrawTool
     */
    public static void drawTransition(DrawTool drawTool) {
        if (Scene.transition != null) {
            Scene.transition.transition().draw(drawTool);
        }
    }

    /**
     * Schließt die aktuelle Szene und setzt current auf GameScene.
     */
    public static void close() {
        Scene.current.onClose(null);
        Scene.current = GameScene.getInstance();
    }

    /**
     * @return Registry aller Szenen
     */
    public static HashMap<String, Scene> getScenes() {
        return Scene.scenes;
    }

    /**
     * @return aktuell aktive Szene
     */
    public static Scene getCurrentScene() {
        return Scene.current;
    }

    /**
     * @return Szenen-ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return Liste aller VisualModels der Szene
     */
    public List<VisualModel> getVisuals() {
        return this.visuals;
    }

    /**
     * Sucht ein VisualModel anhand seiner ID.
     *
     * @param id Visual-ID
     * @return VisualModel oder null
     */
    public VisualModel getVisual(String id) {
        return this.visuals.stream().filter(_vis -> _vis.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Zeichnet alle sichtbaren VisualModels und optional die GUI.
     *
     * @param drawTool DrawTool
     */
    public void draw(DrawTool drawTool) {
        for (VisualModel visual : this.visuals) {
            if (visual.isVisible()) {
                visual.draw(drawTool);
            }
        }
        if (this.currentGui != null) this.currentGui.draw(drawTool);
    }

    /**
     * Aktualisiert alle VisualModels und optional die GUI.
     *
     * @param dt delta time
     */
    public void update(double dt) {
        for (VisualModel visual : this.visuals) {
            visual.update(dt);
        }
        if (this.currentGui != null) this.currentGui.update(dt);
    }

    /**
     * Callback beim Öffnen der Szene.
     *
     * @param last vorherige Szene (kann null sein)
     */
    public void onOpen(Scene last) {}

    /**
     * Callback beim Schließen der Szene.
     *
     * @param newScene nächste Szene (kann null sein)
     */
    public void onClose(Scene newScene) {}

    /**
     * Mouse-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e MouseEvent
     */
    public void mouseEntered(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseEntered(e);
    }

    /**
     * Mouse-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e MouseEvent
     */
    public void mouseExited(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseExited(e);
    }

    /**
     * Mouse-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e MouseEvent
     */
    public void mouseReleased(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseReleased(e);
    }

    /**
     * Mouse-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e MouseEvent
     */
    public void mouseClicked(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseClicked(e);
    }

    /**
     * Mouse-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e MouseEvent
     */
    public void mouseDragged(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseDragged(e);
    }

    /**
     * Mouse-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e MouseEvent
     */
    public void mouseMoved(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mouseMoved(e);
    }

    /**
     * Mouse-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e MouseEvent
     */
    public void mousePressed(MouseEvent e) {
        if (this.currentGui != null) this.currentGui.mousePressed(e);
    }

    /**
     * Key-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e KeyEvent
     */
    public void keyTyped(KeyEvent e) {
        if (this.currentGui != null) this.currentGui.keyTyped(e);
    }

    /**
     * Öffnet passende GUI per Tastendruck oder leitet Events weiter.
     * ESC schließt die GUI und öffnet ggf. die Parent-GUI.
     *
     * @param e KeyEvent
     */
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

    /**
     * Key-Event Weiterleitung an die aktuelle GUI.
     *
     * @param e KeyEvent
     */
    public void keyReleased(KeyEvent e) {
        if (this.currentGui != null) this.currentGui.keyReleased(e);
    }
}
