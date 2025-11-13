package rise_of_duebel.physics;

import java.security.InvalidParameterException;

public class Interval {

    private double min;
    private double max;

    public Interval(double min, double max) {
        if (min > max) {
            throw new InvalidParameterException(
                String.format("min (%.3f) must be less than max (%.3f)", min, max)
            );
        }
        this.min = min;
        this.max = max;
    }

    public Interval(Interval interval) {
        this.min = interval.min;
        this.max = interval.max;
    }

    public Interval clone() {
        return new Interval(this);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public void setMin(double min) {
        if (min > this.max) {
            throw new InvalidParameterException(
                String.format("min (%.3f) must be less than max (%.3f)", min, this.max)
            );
        }

        this.min = min;
    }


    public void setMax(double max) {
        if (max < this.min) {
            throw new InvalidParameterException(
                String.format("max (%.3f) must be greater than min (%.3f)", max, this.min)
            );
        }

        this.max = max;
    }

    public void set(Interval interval) {
        this.max = interval.max;
        this.min = interval.min;
    }

    public boolean overlaps(Interval interval) {
        return !(this.min > interval.max || interval.min > this.max);
    }

    public double getOverlap(Interval interval) {
        // make sure they overlap
        if (this.overlaps(interval)) {
            return Math.min(this.max, interval.max) - Math.max(this.min, interval.min);
        }
        return 0;
    }

    public boolean containsExclusive(Interval interval) {
        return interval.min > this.min && interval.max < this.max;
    }
}
