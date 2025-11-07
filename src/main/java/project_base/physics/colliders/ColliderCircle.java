package project_base.physics.colliders;

import KAGO_framework.view.DrawTool;
import project_base.Wrapper;
import project_base.physics.*;
import project_base.utils.Vec2;

import java.util.List;
import java.util.UUID;

public class ColliderCircle extends Collider {

    private final Vec2 center;

    public ColliderCircle(BodyType type, double x, double y, double radius) {
        this(UUID.randomUUID().toString(), type, x, y, radius);
    }

    public ColliderCircle(String id, BodyType type, double x, double y, double radius) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.form = ColliderForm.CIRCLE;
        this.colliderClass = "default";
        this.center = new Vec2(this.x + radius, this.y + radius);
        Wrapper.getColliderManager().createBody(this);
    }

    @Override
    public boolean handleCollision(Collider other) {
        switch (other.getForm()) {
            case CIRCLE -> {
                return SAT.fastCircleDetection(this, (ColliderCircle) other);
            }
            case POLYGON, RECTANGLE -> {
                if (AABB.compute(this, other)) {
                    return SAT.compute(this, other);
                }
            }
        }
        return false;
    }

    @Override
    public AABB computeAABB() {
        Vec2 center = this.center;
        double minX = center.x - this.radius;
        double minY = center.y - this.radius;
        double maxX = center.x + this.radius;
        double maxY = center.y + this.radius;
        return new AABB(minX, minY, maxX, maxY);
    }

    @Override
    public void move(double dt) {
        if (this.velocity != null && !this.isDestroyed() && this.velocity.magnitude() > 0) {
            this.center.add(this.velocity.x * dt, this.velocity.y * dt);
        }
    }

    @Override
    protected void moveEntity() {
        if (this.entity != null) {
            this.entity.setX(this.center.x + this.entity.getBodyOffsetX());
            this.entity.setY(this.center.y + this.entity.getBodyOffsetY());
        }
    }

    @Override
    public void setPosition(double x, double y) {
        this.center.set(x, y);
        if (this.entity != null) {
            this.entity.setX(this.center.x + this.entity.getBodyOffsetX());
            this.entity.setY(this.center.y + this.entity.getBodyOffsetY());
        }
    }

    @Override
    public void drawHitbox(DrawTool drawTool) {
        if (!this.isDestroyed()) {
            drawTool.setCurrentColor(this.hitboxColor);
            drawTool.drawCircle(this.getCenter().x, this.getCenter().y, this.radius);
            drawTool.resetColor();
        }
    }

    @Override
    public List<Vec2> getAxes() {
        return null;
    }

    @Override
    public Interval project(Vec2 vector) {
        // project the center onto the given axis
        double c = this.center.clone().dot(vector);
        double r = this.radius;
        // the interval is defined by the radius
        return new Interval(c - r, c + r);
    }

    @Override
    public Vec2 getCenter() {
        return this.center;
    }
}
