package rise_of_duebel.utils;

import com.sun.javafx.geom.Vec2f;
import rise_of_duebel.physics.Epsilon;

public class Vec2 extends com.sun.javafx.geom.Vec2d {

    public Vec2() {}

    public Vec2(double var1, double var3) {
        this.x = var1;
        this.y = var3;
    }

    public Vec2(Vec2 var1) {
        this.set(var1);
    }

    public Vec2(com.sun.javafx.geom.Vec2d var1) {
        this.set(var1);
    }

    public Vec2(Vec2f var1) {
        this.set(var1);
    }

    public Vec2 set(Double var1, Double var2) {
        if (var1 != null) this.x = var1;
        if (var2 != null) this.y = var2;
        return this;
    }

    public Vec2 add(Vec2 other) {
        if (other != null) {
            this.x += other.x;
            this.y += other.y;
        }
        return this;
    }

    public Vec2 add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vec2 add(double x) {
        this.x += x;
        this.y += x;
        return this;
    }

    public Vec2 sub(Vec2 other) {
        if (other != null) {
            this.x -= other.x;
            this.y -= other.y;
        }
        return this;
    }

    public Vec2 sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vec2 sub(double x) {
        this.x -= x;
        this.y -= x;
        return this;
    }

    public Vec2 mul(Vec2 other) {
        if (other != null) {
            this.x *= other.x;
            this.y *= other.y;
        }
        return this;
    }

    public Vec2 mul(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vec2 mul(double x) {
        this.x *= x;
        this.y *= x;
        return this;
    }

    public Vec2 div(Vec2 other) {
        if (other != null) {
            this.x /= other.x;
            this.y /= other.y;
        }
        return this;
    }

    public Vec2 div(double x, double y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vec2 div(double x) {
        this.x /= x;
        this.y /= x;
        return this;
    }

    public double magnitudeSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public double magnitude() {
        return Math.sqrt(this.magnitudeSquared());
    }

    public Vec2 invert() {
        this.x = -x;
        this.y = -y;
        return this;
    }

    public Vec2 normalize() {
        double m = this.magnitude();
        if (m <= Epsilon.E) return new Vec2(this.x, this.y);
        this.x = this.x / m;
        this.y = this.y / m;
        return this;
    }

    public double dot(Vec2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vec2 left() {
        double temp = this.x;
        this.x = this.y;
        this.y = -temp;
        return this;
    }

    public Vec2 right() {
        double temp = this.x;
        this.x = -this.y;
        this.y = temp;
        return this;
    }

    public double cross(Vec2 vector) {
        return this.x * vector.y - this.y * vector.x;
    }

    public double cross(double x, double y) {
        return this.x * y - this.y * x;
    }

    public boolean isZero() {
        return this.x == 0 && this.y == 0;
    }

    public Vec2 abs() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        return this;
    }

    public Vec2 product(double scalar) {
        return new Vec2(this.x * scalar, this.y * scalar);
    }

    public Vec2 clone() {
        return new Vec2(this.x, this.y);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
