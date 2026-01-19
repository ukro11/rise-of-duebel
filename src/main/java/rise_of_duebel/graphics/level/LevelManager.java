package rise_of_duebel.graphics.level;

import KAGO_framework.model.abitur.datenstrukturen.Queue;
import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.Wrapper;
import rise_of_duebel.graphics.level.impl.LevelStats;
import rise_of_duebel.graphics.map.TileMap;
import rise_of_duebel.model.scene.Scene;
import rise_of_duebel.model.scene.impl.GameScene;
import rise_of_duebel.model.scene.impl.WinScene;
import rise_of_duebel.model.sound.SoundManager;
import rise_of_duebel.model.transitions.DefaultTransition;
import rise_of_duebel.model.transitions.Transition;

import java.util.HashMap;
import java.util.List;

/***
 * @author Mark
 */
public class LevelManager {

    private static final Logger log = LoggerFactory.getLogger(LevelManager.class);

    public Queue<LevelSwitch> levelSwitchQueue;
    private LevelSwitch transition;

    private HashMap<Integer, LevelMap> cache;

    private LevelMap previous;
    private LevelMap current;
    private LevelMap next;

    private int index;

    /**
     * Erstellt den LevelManager und setzt Startlevel sowie Stats-Level als nächstes.
     *
     * @param levelStart Start-Levelindex
     */
    public LevelManager(int levelStart) {
        this.cache = new HashMap<>();
        this.index = levelStart;
        this.levelSwitchQueue = new Queue<>();
        this.previous = null;
        this.current = this.cache.getOrDefault(this.index, this.getMapByIndex(this.index));
        this.next = this.getStatsMap();
    }

    /** Aktiviert das Startlevel (Loader-Callback). */
    public void loadStartLevel() {
        this.current.onActive();
    }

    /**
     * @return LevelMap für die Stats-Ansicht (Index -1)
     */
    private LevelMap getStatsMap() {
        return this.cache.getOrDefault(-1, new LevelMap("/levels/stats/stats.json", LevelStats.class, List.of(), List.of(), List.of("*"), List.of()));
    }

    /**
     * Zeichnet das aktuelle Level.
     *
     * @param drawTool DrawTool
     */
    public void draw(DrawTool drawTool) {
        if (this.current != null) {
            this.current.draw(drawTool);
        }
    }

    /**
     * Zeichnet Inhalte, die nach dem Player gerendert werden sollen.
     *
     * @param drawTool DrawTool
     */
    public void drawAfterPlayer(DrawTool drawTool) {
        if (this.current != null) {
            this.current.drawAfterPlayer(drawTool);
        }
    }

    /**
     * Aktualisiert das aktuelle Level sowie aktive Transition/Queue-Logik.
     *
     * @param dt delta time
     */
    public void update(double dt) {
        if (this.current != null) {
            this.current.update(dt);
        }

        if (this.transition != null) {
            LevelMap last = this.transition.last();
            LevelMap next = this.transition.next();
            Transition tr = this.transition.transition();

            if (tr.swap()) {
                if (this.current != next) {
                    if (this.transition.dir() == LevelSwitch.LevelSwitchDirection.NEXT) {
                        this.setNextLevel();

                    } else {
                        this.setPreviousLevel();
                    }
                    if (this.transition.runnable() != null) this.transition.runnable().run();
                    tr.out(last, next);
                }
                if (tr.finished()) {
                    this.transition = null;
                }
            }
        } else {
            if (!this.levelSwitchQueue.isEmpty()) {
                this.transition = this.levelSwitchQueue.front();
                this.levelSwitchQueue.dequeue();
            }
        }
    }

    /**
     * Zeichnet die aktuelle Transition (falls aktiv).
     *
     * @param drawTool DrawTool
     */
    public void drawTransition(DrawTool drawTool) {
        if (this.transition != null) {
            this.transition.transition().draw(drawTool);
        }
    }

    /**
     * Lädt eine LevelMap anhand des Index, sofern die Map-Datei existiert.
     * Versucht zusätzlich den passenden LevelLoader zu finden.
     *
     * @param index Levelindex
     * @return LevelMap oder null
     */
    private LevelMap getMapByIndex(int index) {
        String pathNext = String.format("/levels/%d/level%d.json", index, index);
        if (TileMap.mapExists(pathNext)) {
            Class<LevelLoader> loader = LevelLoader.getLoaderByIndex(index);
            if (loader != null) {
                return new LevelMap(pathNext, loader, List.of(), List.of(), List.of("*"), List.of());

            } else {
                return new LevelMap(pathNext, null, List.of(), List.of(), List.of("*"), List.of());
            }
        }
        return null;
    }

    /**
     * Startet (oder queued) einen Levelwechsel mit Richtung und Transition.
     *
     * @param id eindeutige Wechsel-ID (z.B. Event-Quelle)
     * @param direction Richtung des Wechsels
     * @param level Ziel-Level
     * @param runWhileTransition optionaler Code, der beim Swap ausgeführt wird
     * @param transition Transition-Implementierung
     */
    private void initiateNewLevel(String id, LevelSwitch.LevelSwitchDirection direction, LevelMap level, Runnable runWhileTransition, Transition transition) {
        if (level == null) {
            log.warn("Cancelled level change because level is null");
            return;
        }
        LevelSwitch levelSwitch = new LevelSwitch(id, direction, this.current, level, runWhileTransition, transition);
        if (this.transition == null) {
            this.transition = levelSwitch;
            transition.in(this.current);

        } else if (!this.transition.id().equals(id)) {
            if (!this.levelSwitchQueue.isEmpty() && !this.levelSwitchQueue.front().id().equals(id) && !this.levelSwitchQueue.tail().id().equals(id)) {
                this.levelSwitchQueue.enqueue(levelSwitch);
            }
        }
        if (this.next.getLoader() instanceof LevelStats) {
            Wrapper.getSoundConstants().SOUND_WIN.setVolume(0.85);
            SoundManager.playSound(Wrapper.getSoundConstants().SOUND_WIN, false);
        }
        if (!(this.next.getLoader() instanceof LevelStats)) {
            this.current.getLoader().getUserProfiles().forEach(u -> u.resetDeaths());
        }
    }

    /**
     * Wechselt zum nächsten Level mit DefaultTransition.
     *
     * @param id Wechsel-ID
     */
    public void nextLevel(String id) {
        this.nextLevel(id, null, new DefaultTransition());
    }

    /**
     * Wechselt zum nächsten Level mit DefaultTransition.
     *
     * @param id Wechsel-ID
     * @param runWhileTransition optionaler Code beim Swap
     */
    public void nextLevel(String id, Runnable runWhileTransition) {
        this.nextLevel(id, runWhileTransition, new DefaultTransition());
    }

    /**
     * Wechselt zum nächsten Level (nur in der GameScene).
     * Falls nach den Stats kein nächstes Level existiert, wird die WinScene geöffnet.
     *
     * @param id Wechsel-ID
     * @param runWhileTransition optionaler Code beim Swap
     * @param transition Transition-Implementierung
     */
    public void nextLevel(String id, Runnable runWhileTransition, Transition transition) {
        if (Scene.getCurrentScene() != GameScene.getInstance()) return;
        if (this.next != null && this.next.getLoader() instanceof LevelStats) {
            LevelMap nnext = this.cache.getOrDefault(this.index + 1, this.getMapByIndex(this.index + 1));
            if (nnext == null) {
                Scene.open(new WinScene(), new DefaultTransition());
                return;
            }
        }
        this.initiateNewLevel(id, LevelSwitch.LevelSwitchDirection.NEXT, this.next, runWhileTransition, transition);
    }

    /**
     * Wechselt zum vorherigen Level mit DefaultTransition.
     *
     * @param id Wechsel-ID
     */
    public void previousLevel(String id) {
        this.previousLevel(id, new DefaultTransition());
    }

    /**
     * Wechselt zum vorherigen Level.
     *
     * @param id Wechsel-ID
     * @param transition Transition-Implementierung
     */
    public void previousLevel(String id, Transition transition) {
        this.initiateNewLevel(id, LevelSwitch.LevelSwitchDirection.PREVIOUS, this.previous, null, transition);
    }

    /**
     * Wechselt zum vorherigen Level.
     *
     * @param id Wechsel-ID
     * @param runWhileTransition optionaler Code beim Swap
     * @param transition Transition-Implementierung
     */
    public void previousLevel(String id, Runnable runWhileTransition, Transition transition) {
        this.initiateNewLevel(id, LevelSwitch.LevelSwitchDirection.PREVIOUS, this.previous, runWhileTransition, transition);
    }

    /**
     * Setzt current auf next und aktualisiert previous/next sowie Index.
     * Stats-Level werden als Zwischenstufe verwendet.
     */
    private void setNextLevel() {
        if (this.current != null) log.info("NEXT SWITCH 1 {}", this.current.getLoader());
        if (this.next != null) log.info("NEXT SWITCH 2 {}", this.next.getLoader());

        this.previous = this.current;
        if (this.next != null && !(this.next.getLoader() instanceof LevelStats)) {
            this.next.getLoader().resetLevel();
        }

        this.current.onHide();
        this.current = this.next;
        this.current.onActive();
        if (!(this.current.getLoader() instanceof LevelStats)) {
            this.next = this.getStatsMap();
            this.index++;

        } else {
            this.next = this.cache.getOrDefault(this.index + 1, this.getMapByIndex(this.index + 1));
        }
    }

    /**
     * Setzt current auf previous und aktualisiert previous/next sowie Index.
     */
    private void setPreviousLevel() {
        if (this.previous == null) return;

        boolean wasStats = (this.current != null && this.current.getLoader() instanceof LevelStats);

        this.next = this.current;

        this.previous.getLoader().resetLevel();
        this.current.onHide();

        this.current = this.previous;
        this.current.onActive();

        if (!wasStats && this.index != 1) {
            this.index--;
        }

        if (this.index > 1) {
            this.previous = this.cache.getOrDefault(this.index - 1, this.getMapByIndex(this.index - 1));
        } else {
            this.previous = null;
        }
    }

    /**
     * @return vorheriges Level
     */
    public LevelMap getPrevious() {
        return this.previous;
    }

    /**
     * @return aktuelles Level
     */
    public LevelMap getCurrent() {
        return this.current;
    }

    /**
     * @return nächstes Level
     */
    public LevelMap getNext() {
        return this.next;
    }

    /**
     * @return aktueller Levelindex
     */
    public int getIndex() {
        return this.index;
    }
}
