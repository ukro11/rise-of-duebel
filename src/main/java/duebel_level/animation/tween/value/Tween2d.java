package duebel_level.animation.tween.value;

import duebel_level.animation.tween.Tween;

import java.util.function.Function;

public class Tween2d extends TweenValue<Double> {

    public Tween2d(double start, double target, Tween.TweenDirection direction) {
        super(start, target, direction);
    }

    @Override
    public Double animateValue(Function<Double, Double> easing, double elapsed, double duration) {
        if (this.isFinished()) return this.value;
        double t = easing.apply(elapsed / duration);
        this.value = this.start + (this.target - this.start) * t;
        /*if (t == 1 && this.direction == Tween.TweenDirection.ALTERNATE && !this.finishedOnce) {
            this.finishedOnce = true;
            this.target = this.start;
        }*/
        return this.value;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
