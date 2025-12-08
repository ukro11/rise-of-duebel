package rise_of_duebel;

import KAGO_framework.control.ViewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.animation.tween.Tween;
import rise_of_duebel.event.events.KeyPressedEvent;
import rise_of_duebel.event.services.EventProcessCallback;
import rise_of_duebel.event.services.process.EventLoadAssetsProcess;
import rise_of_duebel.graphics.map.GsonMap;
import rise_of_duebel.graphics.map.TileMap;
import rise_of_duebel.model.debug.impl.InfoComponent;
import rise_of_duebel.model.entity.player.EntityPlayer;
import rise_of_duebel.model.scene.GameScene;
import rise_of_duebel.physics.BodyType;
import rise_of_duebel.physics.Collider;
import rise_of_duebel.physics.colliders.ColliderRectangle;
import rise_of_duebel.utils.CooldownManager;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Ein Objekt der Klasse ProgramController dient dazu das Programm zu steuern.
 * Hinweise:
 * - Der Konstruktor sollte nicht geändert werden.
 * - Sowohl die startProgram()- als auch die updateProgram(...)-Methoden müssen vorhanden sein und ihre Signatur sollte
 *   nicht geändert werden
 * - Zusätzliche Methoden sind natürlich gar kein Problem
 */
public class ProgramController {

    private final Logger logger = LoggerFactory.getLogger(ProgramController.class);

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
        Wrapper.getProcessManager().queue(new EventLoadAssetsProcess("Loading map", () -> {
            // /temp/undead/Tiled_files/Undead_land.json
            // /map/kitchen.json
            // /map/overworld/Undead_land.json
            Wrapper.getMapManager().importMap(new TileMap("/map/overworld/Undead_land.json", List.of(), List.of(), List.of("*"), List.of()) {
                @Override
                public void loadCollider(GsonMap.Layer layer, GsonMap.ObjectCollider objCollider, Collider collider) {

                }
            });
            Wrapper.getMapManager().showMap(0);

        }, new EventProcessCallback() {
            @Override
            public void onSuccess(Object data) {
                viewController.continueStart();
            }
        }));
    }

    /***
     * Diese Methode wird genau ein mal nach Programmstart aufgerufen. Hier sollte also alles geregelt werden,
     * was zu diesem Zeipunkt passieren muss.
     */
    public void startProgram() {
        if (Config.RUN_ENV == Config.Environment.DEVELOPMENT) {
            Wrapper.getEventManager().addEventListener("keypressed", (KeyPressedEvent event) -> {
                if (event.getKeyCode() == KeyEvent.VK_F9) {
                    this.viewController.setWatchPhyics(!this.viewController.watchPhysics());
                }
            });
        }
        GameScene.getInstance().getVisuals().add(new InfoComponent());

        this.player = Wrapper.getEntityManager().spawnPlayer("player", 270, 190);
        this.player.setShowHitbox(false);

        new ColliderRectangle(BodyType.STATIC, 0, 500, 400, 100);
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
