package project_base.animation.tween;

import project_base.animation.Easings;
import project_base.animation.tween.value.Tween2d;
import project_base.animation.tween.value.Tween2i;
import project_base.animation.tween.value.TweenValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class Tween<T extends Number, Q extends TweenValue> {

    private static CopyOnWriteArrayList<Tween<?, ?>> tweens = new CopyOnWriteArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(Tween.class);

    protected Q value;
    protected T target;
    protected TweenDirection direction;
    protected double duration;
    protected double delay;
    protected boolean loop;
    protected boolean stayAlive;
    protected Function<Double, Double> easing;

    protected T start;
    protected double elapsed;
    protected double delayElapsed;
    protected boolean running;
    protected boolean paused;
    private boolean delayFinished;

    protected Consumer<Tween<T, Q>> onStart;
    protected Consumer<Tween<T, Q>> onStop;
    protected Consumer<Tween<T, Q>> onPause;
    protected Consumer<Tween<T, Q>> onResume;
    protected Consumer<Tween<T, Q>> onUpdate;
    protected Consumer<Tween<T, Q>> onFinish;

    protected Tween<?, ?> after;

    private Tween(T value, T target, double duration) {
        this.start = value;
        this.target = target;
        this.duration = duration;
        this.easing = (x) -> Easings.linear(x);
        this.elapsed = 0;
        this.running = false;
        this.stayAlive = false;
        this.delayFinished = true;
        this.direction = TweenDirection.DEFAULT;

        if (value instanceof Double) {
            this.value = (Q) new Tween2d((double) value, (double) target, this.direction);

        } else if (value instanceof Integer) {
            this.value = (Q) new Tween2i((int) value, (int) target, this.direction);

        } else {
            this.logger.warn("Tween only supports doubles and integers");
        }
    }

    public static <T extends Number, Q extends TweenValue> Tween<T, Q> to(T value, T target, double duration) {
        return new Tween<T, Q>(value, target, duration);
    }

    public void update(double dt) {
        if (this.value == null) return;

        if (this.isRunning() && !this.isPaused()) {
            if (!this.delayFinished) {
                this.delayElapsed += dt;
                this.delayElapsed = Math.min(this.delayElapsed, this.delay);
                if (this.delayElapsed >= this.delay) {
                    this.delayFinished = true;
                }
                return;
            }
            this.elapsed += dt;
            this.elapsed = Math.min(this.elapsed, this.duration);

            this.value.animateValue(this.easing, this.elapsed, this.duration);
            if (this.onUpdate != null) this.onUpdate.accept(this);

            if (this.isFinished()) {
                if (this.isLoop()) {
                    this.elapsed = 0;

                } else {
                    if (!this.stayAlive) {
                        this.running = false;
                        Tween.tweens.remove(this);
                        if (this.after != null) this.after.animate();

                    } else if (this.after != null) {
                        this.logger.warn("Tween \"after\" function will not be called because your tween should stay alive");
                    }
                }
                if (this.onFinish != null) this.onFinish.accept(this);
            }
        }
    }

    public void animate() {
        this.reset();
        this.running = true;
        if (!Tween.tweens.contains(this)) Tween.tweens.add(this);
        if (this.onStart != null) this.onStart.accept(this);
    }

    public void stop() {
        this.stop(false);
    }

    public void stop(boolean callFinish) {
        this.running = false;
        this.reset();
        if (this.onStop != null) this.onStop.accept(this);
        if (this.onFinish != null && callFinish) this.onFinish.accept(this);
    }

    public Tween<T, Q> ease(Function<Double, Double> easing) {
        this.easing = easing;
        return this;
    }

    public Tween<T, Q> duration(double duration) {
        this.duration = duration;
        return this;
    }

    public Tween<T, Q> direction(TweenDirection direction) {
        this.direction = direction;
        return this;
    }

    public Tween<T, Q> delay(double delay) {
        this.delay = delay;
        if (delay > 0) {
            this.delayFinished = false;
        }
        return this;
    }

    public Tween<T, Q> loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public Tween<T, Q> stayAlive(boolean stayAlive) {
        this.stayAlive = stayAlive;
        return this;
    }

    public Tween<T, Q> resume() {
        this.paused = false;
        if (this.onResume != null) this.onResume.accept(this);
        return this;
    }

    public Tween<T, Q> pause() {
        this.paused = true;
        if (this.onPause != null) this.onPause.accept(this);
        return this;
    }

    public Tween<T, Q> reset() {
        if (this.start instanceof Double) {
            this.value = (Q) new Tween2d((double) this.start, (double) this.target, this.direction);

        } else if (this.start instanceof Integer) {
            this.value = (Q) new Tween2i((int) this.start, (int) this.target, this.direction);
        }
        this.elapsed = 0;
        return this;
    }

    public Tween<T, Q> set(T value) {
        this.value.setValue(value);
        return this;
    }

    public Tween<T, Q> redo(T value, T target, double duration) {
        this.start = value;
        this.target = target;
        this.duration = duration;
        this.elapsed = 0;
        this.running = false;

        if (value instanceof Double) {
            this.value = (Q) new Tween2d((double) value, (double) target, this.direction);

        } else if (value instanceof Integer) {
            this.value = (Q) new Tween2i((int) value, (int) target, this.direction);

        } else {
            this.logger.warn("Tween only supports doubles and integers");
        }
        return this;
    }

    public Tween<T, Q> onStart(Consumer<Tween<T, Q>> onStart) {
        this.onStart = onStart;
        return this;
    }

    public Tween<T, Q> onStop(Consumer<Tween<T, Q>> onStop) {
        this.onStop = onStop;
        return this;
    }

    public Tween<T, Q> onResume(Consumer<Tween<T, Q>> onResume) {
        this.onResume = onResume;
        return this;
    }

    public Tween<T, Q> onPause(Consumer<Tween<T, Q>> onPause) {
        this.onPause = onPause;
        return this;
    }

    public Tween<T, Q> onUpdate(Consumer<Tween<T, Q>> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public Tween<T, Q> onFinish(Consumer<Tween<T, Q>> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean isLoop() {
        return this.loop;
    }

    public double getDuration() {
        return this.duration;
    }

    public double getDelay() {
        return this.delay;
    }

    public TweenValue getTweenValue() {
        return this.value;
    }

    public T getValue() {
        return (T) this.value.getValue();
    }

    public double getValueDouble() {
        return (double) this.value.getValue();
    }

    public int getValueInt() {
        return (int) this.value.getValue();
    }

    public boolean isFinished() {
        return this.elapsed >= this.duration;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public static void updateAll(double dt) {
        for (Tween<?, ?> tween : Tween.tweens) {
            tween.update(dt);
            if (tween.isFinished() && !tween.isLoop()) {
                Tween.tweens.remove(tween);
            }
        }
    }

    @Override
    public String toString() {
        return "Tween{" +
                "value=" + value +
                ", start=" + start +
                ", target=" + target +
                ", duration=" + duration +
                ", delay=" + delay +
                ", loop=" + loop +
                ", stayAlive=" + stayAlive +
                ", easing=" + easing +
                ", elapsed=" + elapsed +
                ", running=" + running +
                '}';
    }

    public enum TweenDirection {
        DEFAULT,
        //REVERSE,
        //ALTERNATE,
        //ALTERNATE_REVERSE
    }
}
