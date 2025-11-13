package rise_of_duebel.animation.tween.value;

import rise_of_duebel.animation.tween.Tween;

import java.util.function.Function;

public abstract class TweenValue<T extends Number> {

    private final T cacheStart;
    private final T cacheTarget;
    protected T start;
    protected T target;
    protected T value;
    protected Tween.TweenDirection direction;
    protected boolean finishedOnce;

    public TweenValue(T start, T target, Tween.TweenDirection direction) {
        this.cacheStart = start;
        this.cacheTarget = target;
        this.start = start;
        this.target = target;
        this.value = start;
        this.direction = direction;
        this.finishedOnce = false;
    }

    public abstract T animateValue(Function<Double, Double> easing, double elapsed, double duration);
    public abstract Class<T> getType();

    public void reset() {
        this.value = this.cacheStart;
        this.target = this.cacheTarget;
        this.finishedOnce = false;
    }

    public boolean isFinished() {
        return this.value == this.target && this.finishedOnce;
    }

    public T getValue() {
        return this.value;
    }

    public double getValueDouble() {
        return (double) this.value;
    }

    public int getValueInt() {
        return (int) this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getStart() {
        return this.start;
    }

    public void setStart(T value) {
        this.start = value;
    }

    public T getTarget() {
        return this.target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public boolean isFinishedOnce() {
        return this.finishedOnce;
    }

    public void setDirection(Tween.TweenDirection direction) {
        this.direction = direction;
    }

    public Tween.TweenDirection getDirection() {
        return this.direction;
    }

    @Override
    public String toString() {
        return "TweenValue{" +
                "start=" + start +
                ", target=" + target +
                ", value=" + value +
                '}';
    }
}
