package project_base.physics;

import project_base.utils.Vec2;

import java.security.InvalidParameterException;

public class AABB {

    public double minX;
    public double minY;
    public double maxX;
    public double maxY;

    public AABB(double minX, double minY, double maxX, double maxY) {
        if (minX > maxX)
            throw new InvalidParameterException("minX cannot be greater than maxX");

        if (minY > maxY)
            throw new InvalidParameterException("minY cannot be greater than maxY");

        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public AABB(Vec2 min, Vec2 max) {
        this(min.x, min.y, max.x, max.y);
    }

    public AABB(double radius) {
        this(null, radius);
    }

    public AABB(Vec2 center, double radius) {
        if (radius < 0)
            throw new InvalidParameterException("radius < 0 does not work");

        if (center == null) {
            this.minX = -radius;
            this.minY = -radius;
            this.maxX =  radius;
            this.maxY =  radius;
        } else {
            this.minX = center.x - radius;
            this.minY = center.y - radius;
            this.maxX = center.x + radius;
            this.maxY = center.y + radius;
        }
    }

    public boolean overlaps(AABB aabb) {
        return this.minX <= aabb.maxX &&
                this.maxX >= aabb.minX &&
                this.minY <= aabb.maxY &&
                this.maxY >= aabb.minY;
    }

    public static boolean compute(Collider main, Collider other) {
        if (main.isDestroyed() || other.isDestroyed()) return false;
        return main.computeAABB().overlaps(other.computeAABB());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof AABB) {
            AABB other = (AABB) obj;
            return this.maxX == other.maxX &&
                    this.minX == other.minX &&
                    this.maxY == other.maxY &&
                    this.minY == other.minY;
        }
        return false;
    }
}
