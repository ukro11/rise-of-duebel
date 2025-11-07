package project_base.physics;

public class ChildCollider {

    private final Collider collider;
    private double offsetX;
    private double offsetY;

    public ChildCollider(Collider collider) {
        this(collider, 0, 0);
    }

    public ChildCollider(Collider collider, double offsetX, double offsetY) {
        this.collider = collider;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Collider getCollider() {
        return this.collider;
    }

    public double getOffsetX() {
        return this.offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return this.offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }
}
