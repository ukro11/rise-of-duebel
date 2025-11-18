package rise_of_duebel.animation;

import rise_of_duebel.utils.CacheManager;

import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public AnimationRenderer(String spriteSheetPath, int rows, int maxColumns, int frameWidth, int frameHeight, T state) {
        this(spriteSheetPath, rows, maxColumns, frameWidth, frameHeight, 0, 0, state);
    }

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

    public void start() {
        this.currentIndex = 0;
        this.elapsed = 0;
        this.running = true;
        this.paused = false;
    }

    public void pause() {
        this.paused = true;
    }

    public void pauseAtEnd() {
        if (this.currentAnimation != null) {
            this.pause();
            this.currentIndex = this.currentAnimation.getFrames().size() - 1;
            this.elapsed = this.currentAnimation.getDuration();
        }
    }

    public void resume() {
        this.paused = false;
    }

    public void gotoFrame(int index) {
        this.currentIndex = index;
        this.elapsed = 0;
    }

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
                if (this.getFinishRunnable() != null) this.getFinishRunnable().run();
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

    public void onStart(Runnable onStart) {
        this.onStart.put(this.currentAnimation.getState(), onStart);
    }

    public void onStart(T state, Runnable onStart) {
        this.onStart.put(state, onStart);
    }

    public void onCycle(Runnable onCycle) {
        this.onCycle.put(this.currentAnimation.getState(), onCycle);
    }

    public void onCycle(T state, Runnable onCycle) {
        this.onCycle.put(state, onCycle);
    }

    public void onFinish(Runnable onFinish) {
        this.onFinish.put(this.currentAnimation.getState(), onFinish);
    }

    public void onFinish(T state, Runnable onFinish) {
        this.onFinish.put(state, onFinish);
    }

    public double getDuration() {
        if (this.currentAnimation == null) {
            return 0;
        }
        return this.currentAnimation.getDuration();
    }

    public boolean isLoop() {
        if (this.currentAnimation == null) {
            return false;
        }
        return this.currentAnimation.isLoop();
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public HashMap<T, Animation<T>> getAnimations() {
        return this.animations;
    }

    public Animation<T> getCurrentAnimation() {
        return this.currentAnimation;
    }

    public BufferedImage getCurrentFrame() {
        if (this.currentAnimation == null) {
            return null;
        }
        return this.currentAnimation.getFrames().get(this.currentIndex);
    }

    private Runnable getStartRunnable() {
        return this.onStart.get(this.currentAnimation.getState());
    }

    private Runnable getCycleRunnable() {
        return this.onCycle.get(this.currentAnimation.getState());
    }

    private Runnable getFinishRunnable() {
        return this.onFinish.get(this.currentAnimation.getState());
    }
}
