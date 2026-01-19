package rise_of_duebel.animation;

import rise_of_duebel.utils.CacheManager;

import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/***
 * @author Mark
 */
public class AnimationRenderer<T extends Enum<T> & IAnimationState> {

    private final HashMap<T, Animation<T>> animations;

    private HashMap<T, Runnable> onStart;
    private HashMap<T, Runnable> onCycle;
    private HashMap<T, Runnable> onFinish;
    private Animation<T> currentAnimation;
    private int currentIndex = 0;
    private double elapsed;
    private boolean running = false;
    private boolean paused = false;

    /**
     * Erstellt einen Renderer aus einem SpriteSheet ohne Margins.
     *
     * @param spriteSheetPath Pfad zum SpriteSheet
     * @param rows Anzahl Reihen
     * @param maxColumns maximale Spalten pro Reihe
     * @param frameWidth Standard-Framebreite
     * @param frameHeight Standard-Framehöhe
     * @param state Start-State
     */
    public AnimationRenderer(String spriteSheetPath, int rows, int maxColumns, int frameWidth, int frameHeight, T state) {
        this(spriteSheetPath, rows, maxColumns, frameWidth, frameHeight, 0, 0, state);
    }

    /**
     * Erstellt einen Renderer aus einem SpriteSheet mit optionalen Margins.
     * Frames/States werden über IAnimationState.fetch pro Zelle gesammelt.
     *
     * @param spriteSheetPath Pfad zum SpriteSheet
     * @param rows Anzahl Reihen
     * @param maxColumns maximale Spalten pro Reihe
     * @param frameWidth Standard-Framebreite
     * @param frameHeight Standard-Framehöhe
     * @param marginX Abstand in X zwischen Frames
     * @param marginY Abstand in Y zwischen Frames
     * @param state Start-State
     */
    public AnimationRenderer(String spriteSheetPath, int rows, int maxColumns, int frameWidth, int frameHeight, int marginX, int marginY, T state) {
        BufferedImage spriteSheet = CacheManager.loadImage(spriteSheetPath);
        HashMap<T, Animation<T>> animations = new HashMap<>();
        Class<T> enumClass = state.getDeclaringClass();
        for (int i = 0; i < rows; i++) {
            List<BufferedImage> frames = new ArrayList<>();
            for (int j = 0; j < maxColumns; j++) {
                List<T> animationStates = IAnimationState.fetch(enumClass, i, j);
                if (animationStates == null || animationStates.isEmpty()) break;

                BufferedImage animationImage = null;
                int customFrameWidth = animationStates.get(0).getFrameWidth();
                int customFrameHeight = animationStates.get(0).getFrameHeight();

                if (customFrameWidth == 0 || customFrameHeight == 0) {
                    animationImage = spriteSheet.getSubimage(j * (frameWidth + marginX), i * (frameHeight + marginY), frameWidth, frameHeight);

                } else {
                    animationImage = spriteSheet.getSubimage(j * (customFrameWidth + marginX), i * (customFrameHeight + marginY), customFrameWidth, customFrameHeight);
                }

                frames.add(animationImage);

                int ref = j;
                AtomicBoolean clear = new AtomicBoolean(false);
                animationStates.forEach(animationState -> {
                    if (animationState.getColumnRange().upperEndpoint() == ref) {
                        var f = frames.subList(0, frames.size());
                        if (animationState.isReverse()) Collections.reverse(f);
                        animations.put(animationState, new Animation<T>(animationState, f, animationState.getDuration(), animationState.isLoop(), animationState.isReverse()));
                        clear.set(true);
                    }
                });
                if (clear.get()) frames.clear();
            }
        }
        this.animations = animations;
        this.onStart = new HashMap<>();
        this.onCycle = new HashMap<>();
        this.onFinish = new HashMap<>();

        if (this.animations.values().stream().anyMatch(f -> this.animations.values().stream().filter(_f -> f.getState().equals(_f.getState())).count() > 1)) {
            throw new InvalidParameterException("Atleast 2 framesLists have been found with the same state.");
        }

        this.currentAnimation = this.animations.values().stream().filter(s -> s.getState().equals(state)).findFirst().orElse(null);

        if (this.currentAnimation == null) throw new NullPointerException(String.format("You did not passed an animation with the state: %s", state.name()));
    }

    /**
     * Erstellt einen Renderer aus bereits fertigen Animationen.
     *
     * @param animations Animation-Liste
     * @param state Start-State
     */
    public AnimationRenderer(List<Animation<T>> animations, T state) {
        HashMap<T, Animation<T>> map = new HashMap<>();
        for (Animation<T> animation : animations) {
            map.put(animation.getState(), animation);
        }
        this.animations = map;

        if (this.animations.values().stream().anyMatch(f -> this.animations.values().stream().filter(_f -> f.getState().equals(_f.getState())).count() > 1)) {
            throw new InvalidParameterException("Atleast 2 framesLists have been found with the same state.");
        }

        this.currentAnimation = this.animations.values().stream().filter(s -> s.getState().equals(state)).findFirst().get();

        if (this.currentAnimation == null) throw new NullPointerException(String.format("You did not passed an animation with the state: %s", state.name()));
    }

    /** Startet die aktuelle Animation von vorne. */
    public void start() {
        this.currentIndex = 0;
        this.elapsed = 0;
        this.running = true;
        this.paused = false;
    }

    /** Pausiert die Animation. */
    public void pause() {
        this.paused = true;
    }

    /** Pausiert und springt auf das letzte Frame der aktuellen Animation. */
    public void pauseAtEnd() {
        if (this.currentAnimation != null) {
            this.pause();
            this.currentIndex = this.currentAnimation.getFrames().size() - 1;
            this.elapsed = this.currentAnimation.getDuration();
        }
    }

    /** Setzt Pause zurück. */
    public void resume() {
        this.paused = false;
    }

    /**
     * Springt auf einen Frame-Index und setzt elapsed zurück.
     *
     * @param index Frame-Index
     */
    public void gotoFrame(int index) {
        this.currentIndex = index;
        this.elapsed = 0;
    }

    /**
     * Wechselt die Animation anhand des States (falls vorhanden).
     *
     * @param state neuer State
     */
    public void switchState(T state) {
        if (this.currentAnimation == null || this.currentAnimation.getState() != state) {
            if (this.animations.get(state) != null) {
                this.currentAnimation = this.animations.get(state);
                this.currentIndex = this.currentAnimation.isReverse() ? this.currentAnimation.getFrames().size() - 1 : 0;
                this.elapsed = 0;
                this.paused = false;
            }
        }
    }

    /**
     * Aktualisiert Frame-Index und führt ggf. Start/Cycle/Finish-Callbacks aus.
     *
     * @param dt delta time
     */
    public void update(double dt) {
        if (!this.running || this.paused || this.currentAnimation == null || this.currentAnimation.getFrames().size() <= 1) return;
        if (this.elapsed == 0 && this.currentIndex == 0 && this.getStartRunnable() != null) this.getStartRunnable().run();
        this.elapsed += dt;
        Animation animation = this.currentAnimation;
        int size = animation.getFrames().size();
        if (this.elapsed >= animation.getDurationPerFrame()) {
            boolean lastIndex = animation.isReverse() ? this.currentIndex == 0 : this.currentIndex == size - 1;
            if (lastIndex) {
                int max = animation.isReverse() ? size - 1 : 0;
                if (this.getFinishRunnable() != null) this.onFinish.remove(this.currentAnimation.getState()).run();
                if (animation.isLoop()) {
                    this.currentIndex = max;
                }
            } else {
                if (this.getCycleRunnable() != null) this.getCycleRunnable().run();
                if (animation.isReverse()) {
                    this.currentIndex--;
                } else {
                    this.currentIndex++;
                }
            }
            this.elapsed = 0;
        }
    }

    /**
     * Setzt einen Start-Callback für den aktuellen State.
     *
     * @param onStart Callback
     */
    public void onStart(Runnable onStart) {
        this.onStart.put(this.currentAnimation.getState(), onStart);
    }

    /**
     * Setzt einen Start-Callback für einen State.
     *
     * @param state State
     * @param onStart Callback
     */
    public void onStart(T state, Runnable onStart) {
        this.onStart.put(state, onStart);
    }

    /**
     * Setzt einen Cycle-Callback für den aktuellen State.
     *
     * @param onCycle Callback
     */
    public void onCycle(Runnable onCycle) {
        this.onCycle.put(this.currentAnimation.getState(), onCycle);
    }

    /**
     * Setzt einen Cycle-Callback für einen State.
     *
     * @param state State
     * @param onCycle Callback
     */
    public void onCycle(T state, Runnable onCycle) {
        this.onCycle.put(state, onCycle);
    }

    /**
     * Setzt einen Finish-Callback für den aktuellen State.
     *
     * @param onFinish Callback
     */
    public void onFinish(Runnable onFinish) {
        this.onFinish.put(this.currentAnimation.getState(), onFinish);
    }

    /**
     * Setzt einen Finish-Callback für einen State.
     *
     * @param state State
     * @param onFinish Callback
     */
    public void onFinish(T state, Runnable onFinish) {
        this.onFinish.put(state, onFinish);
    }

    /**
     * @return Gesamtdauer der aktuellen Animation (0 wenn keine aktiv)
     */
    public double getDuration() {
        if (this.currentAnimation == null) {
            return 0;
        }
        return this.currentAnimation.getDuration();
    }

    /**
     * @return true, wenn aktuelle Animation looped
     */
    public boolean isLoop() {
        if (this.currentAnimation == null) {
            return false;
        }
        return this.currentAnimation.isLoop();
    }

    /**
     * @return true, wenn Renderer gestartet wurde
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * @return true, wenn pausiert
     */
    public boolean isPaused() {
        return this.paused;
    }

    /**
     * @return aktueller Frame-Index
     */
    public int getCurrentIndex() {
        return this.currentIndex;
    }

    /**
     * @return Map aller Animationen
     */
    public HashMap<T, Animation<T>> getAnimations() {
        return this.animations;
    }

    /**
     * @return aktuell aktive Animation
     */
    public Animation<T> getCurrentAnimation() {
        return this.currentAnimation;
    }

    /**
     * @return aktuelles Frame oder null
     */
    public BufferedImage getCurrentFrame() {
        if (this.currentAnimation == null) {
            return null;
        }
        return this.currentAnimation.getFrames().get(this.currentIndex);
    }

    /**
     * @return Start-Callback für aktuellen State oder null
     */
    private Runnable getStartRunnable() {
        return this.onStart.get(this.currentAnimation.getState());
    }

    /**
     * @return Cycle-Callback für aktuellen State oder null
     */
    private Runnable getCycleRunnable() {
        return this.onCycle.get(this.currentAnimation.getState());
    }

    /**
     * @return Finish-Callback für aktuellen State oder null
     */
    private Runnable getFinishRunnable() {
        return this.onFinish.get(this.currentAnimation.getState());
    }
}
