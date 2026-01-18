package rise_of_duebel;

import KAGO_framework.control.ViewController;
import rise_of_duebel.animation.tween.Tween;
import rise_of_duebel.event.events.KeyPressedEvent;
import rise_of_duebel.graphics.gui.Gui;
import rise_of_duebel.graphics.gui.impl.GuiPause;
import rise_of_duebel.model.debug.impl.InfoComponent;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.scene.impl.GameScene;
import rise_of_duebel.model.sound.SoundManager;
import rise_of_duebel.utils.CooldownManager;

import java.awt.event.KeyEvent;

/**
 * Ein Objekt der Klasse ProgramController dient dazu das Programm zu steuern.
 * Hinweise:
 * - Der Konstruktor sollte nicht geändert werden.
 * - Sowohl die startProgram()- als auch die updateProgram(...)-Methoden müssen vorhanden sein und ihre Signatur sollte
 *   nicht geändert werden
 * - Zusätzliche Methoden sind natürlich gar kein Problem
 */
public class ProgramController {

    private final ViewController viewController;
    public EntityPlayer player;

    /***
     * Konstruktor
     * Dieser legt das Objekt der Klasse ProgramController an, das den Programmfluss steuert.
     * Damit der ProgramController auf das Fenster zugreifen kann, benötigt er eine Referenz auf das Objekt
     * der Klasse viewController. Diese wird als Parameter übergeben.
     * @param viewController das viewController-Objekt des Programms
     */
    public ProgramController(ViewController viewController){
        this.viewController = viewController;
    }

    /***
     * Wird als aller erstes aufgerufen beim starten. "startProgram" wird hingegen nur
     * nach Erstellen des Fensters, usw. erst aufgerufen.
     */
    public void preStartProgram() {
        this.viewController.continueStart();
    }

    /***
     * Diese Methode wird genau ein mal nach Programmstart aufgerufen. Hier sollte also alles geregelt werden,
     * was zu diesem Zeipunkt passieren muss.
     */
    public void startProgram() {
        Wrapper.getSoundConstants().SOUND_BACKGROUND.setVolume(0.75);
        SoundManager.playSound(Wrapper.getSoundConstants().SOUND_BACKGROUND, true);
        if (Config.RUN_ENV == Config.Environment.DEVELOPMENT) {
            Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
                if (event.getKeyCode() == KeyEvent.VK_F9) {
                    this.viewController.setWatchPhyics(!this.viewController.watchPhysics());
                }
            });
        }
        GameScene.getInstance().getVisuals().add(new InfoComponent());

        Gui.registerGui(new GuiPause());

        this.player = Wrapper.getEntityManager().spawnPlayer(0, 0);
        this.player.setShowHitbox(false);

        this.focusDefault(-1);
        Wrapper.getLevelManager().loadStartLevel();
    }

    public void focusDefault(double zoom) {
        if (zoom == -1) zoom = 2;
        GameScene.getInstance().getCameraRenderer().zoom(zoom);
        GameScene.getInstance().getCameraRenderer().focusAt(375, 475);
    }

    public void focusPlayer(double zoom) {
        if (zoom == -1) zoom = 2;
        GameScene.getInstance().getCameraRenderer().zoom(zoom);
        GameScene.getInstance().getCameraRenderer().focusAtEntity(this.player);
    }

    /***
     * Diese Methode wird gecalled, wenn das Game geschlossen wird
     */
    public void shutdown() {}

    /**
     * Diese Methode wird vom ViewController-Objekt automatisch mit jedem Frame aufgerufen (ca. 60mal pro Sekunde)
     * @param dt Zeit seit letztem Frame in Sekunden
     */
    public void updateProgram(double dt){
        CooldownManager.update(dt);
        Tween.updateAll(dt);
    }
}
