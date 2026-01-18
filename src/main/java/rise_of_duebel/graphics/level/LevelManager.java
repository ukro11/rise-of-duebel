package rise_of_duebel.graphics.level;

import KAGO_framework.model.abitur.datenstrukturen.Queue;
import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.Wrapper;
import rise_of_duebel.graphics.level.impl.LevelStats;
import rise_of_duebel.graphics.map.TileMap;
import rise_of_duebel.model.sound.SoundManager;
import rise_of_duebel.model.transitions.DefaultTransition;
import rise_of_duebel.model.transitions.Transition;

import java.util.HashMap;
import java.util.List;

public class LevelManager {

    private static final Logger log = LoggerFactory.getLogger(LevelManager.class);

    public Queue<LevelSwitch> levelSwitchQueue;
    private LevelSwitch transition;

    private HashMap<Integer, LevelMap> cache;

    private LevelMap previous;
    private LevelMap current;
    private LevelMap next;


    private int index;

    public LevelManager(int levelStart) {
        this.cache = new HashMap<>();
        this.index = levelStart;
        this.levelSwitchQueue = new Queue<>();

        //Wrapper.getProcessManager().queue(new EventLoadAssetsProcess<>("LevelManager loading", () -> {
            this.previous = null;
            this.current = this.cache.getOrDefault(this.index, this.getMapByIndex(this.index));
            this.next = this.getStatsMap();

        //}, new EventProcessCallback<>() {}));
    }

    public void loadStartLevel() {
        this.current.onActive();
    }

    private LevelMap getStatsMap() {
        return this.cache.getOrDefault(-1, new LevelMap("/levels/stats/stats.json", LevelStats.class, List.of(), List.of(), List.of("*"), List.of()));
    }

    public void draw(DrawTool drawTool) {
        if (this.current != null) {
            this.current.draw(drawTool);
        }
    }

    public void drawAfterPlayer(DrawTool drawTool) {
        if (this.current != null) {
            this.current.drawAfterPlayer(drawTool);
        }
    }

    public void update(double dt) {
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
                    tr.out(last, next);
                }
                if (tr.finished()) {
                    log.info("FINISHED");
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

    public void drawTransition(DrawTool drawTool) {
        if (this.transition != null) {
            this.transition.transition().draw(drawTool);
        }
    }

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

    private void initiateNewLevel(String id, LevelSwitch.LevelSwitchDirection direction, LevelMap level, Transition transition) {
        if (level == null) {
            log.warn("Cancelled level change because level is null");
            return;
        }
        LevelSwitch levelSwitch = new LevelSwitch(id, direction, this.current, level, transition);
        if (this.transition == null) {
            this.transition = levelSwitch;
            transition.in(this.current);

        } else if (!this.transition.id().equals(id)) {
            if (!this.levelSwitchQueue.isEmpty() && !this.levelSwitchQueue.front().id().equals(id) && !this.levelSwitchQueue.tail().id().equals(id)) {
                log.info("QUEUE");
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

    public void nextLevel(String id) {
        this.nextLevel(id, new DefaultTransition());
    }

    public void nextLevel(String id, Transition transition) {
        this.initiateNewLevel(id, LevelSwitch.LevelSwitchDirection.NEXT, this.next, transition);
    }

    public void previousLevel(String id) {
        this.previousLevel(id, new DefaultTransition());
    }

    public void previousLevel(String id, Transition transition) {
        this.initiateNewLevel(id, LevelSwitch.LevelSwitchDirection.PREVIOUS, this.previous, transition);
    }

    private void setNextLevel() {
        this.previous = this.current;
        this.next.getLoader().resetLevel();
        this.current.onHide();
        this.current = this.next;
        this.current.onActive();
        if (!(this.current.getLoader() instanceof LevelStats)) {
            this.index++;
        }
        //Wrapper.getProcessManager().queue(new EventLoadAssetsProcess<>("LevelManager loading next map", () -> {
            if (!(this.current.getLoader() instanceof LevelStats)) {
                this.next = this.getStatsMap();

            } else {
                this.next = this.cache.getOrDefault(this.index + 1, this.getMapByIndex(this.index + 1));
            }
        //}, new EventProcessCallback<>() {}));
    }

    private void setPreviousLevel() {
        if (this.previous == null) return;

        // Merken, ob wir gerade in der Stats-Map sind
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

    public LevelMap getPrevious() {
        return this.previous;
    }

    public LevelMap getCurrent() {
        return this.current;
    }

    public LevelMap getNext() {
        return this.next;
    }

    public int getIndex() {
        return this.index;
    }
}
