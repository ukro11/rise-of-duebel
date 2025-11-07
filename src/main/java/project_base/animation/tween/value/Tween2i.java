package project_base.animation.tween.value;

import project_base.animation.tween.Tween;

import java.util.function.Function;

public class Tween2i extends TweenValue<Integer> {

    public Tween2i(int start, int target, Tween.TweenDirection direction) {
        super(start, target, direction);
    }

    @Override
    public Integer animateValue(Function<Double, Double> easing, double elapsed, double duration) {
        if (this.isFinished()) return this.value;
        double t = easing.apply(elapsed / duration);
        this.value = (int) (this.start + (this.target - this.start) * t);
        return this.value;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
