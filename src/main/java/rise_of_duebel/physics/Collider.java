package rise_of_duebel.physics;

import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.Wrapper;
import rise_of_duebel.event.events.collider.ColliderCollisionEvent;
import rise_of_duebel.event.events.collider.ColliderDestroyEvent;
import rise_of_duebel.model.entity.Entity;
import rise_of_duebel.utils.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public abstract class Collider {

    protected final Logger logger = LoggerFactory.getLogger(Collider.class);

    protected String id;
    protected BodyType type;
    protected ColliderForm form;
    protected double x;
    protected double y;
    protected double radius;
    protected double width;
    protected double height;
    protected Vec2 velocity = new Vec2();
    protected double gravity = 0;
    protected final boolean gravityEnabled;
    protected boolean grounded = false;
    protected Entity entity;
    protected String colliderClass;
    protected boolean sensor = false;
    protected Color hitboxColor = Color.RED;
    private boolean destroyed = false;
    protected Collider parent;
    protected ChildCollider childInstance = new ChildCollider(this);
    protected List<ChildCollider> children = new ArrayList<>();

    protected List<Consumer<ColliderCollisionEvent>> onCollision = new ArrayList<>();
    protected List<Consumer<ColliderDestroyEvent>> onDestroy = new ArrayList<>();
    protected List<Consumer<Collider>> onMove = new ArrayList<>();

    private HashMap<String, Boolean> wasColliding = new HashMap<>();

    public Collider(boolean gravity) {
        this(gravity, null);
    }

    public Collider(boolean gravity, ChildCollider foot) {
        this.gravityEnabled = gravity;
        if (gravity) {
            this.children.add(foot == null ? this.createFootColider() : foot);
        }
    }

    /**
     * Setzt die lineare Geschwindigkeit des Bodys in der x- und y-Richtung. Damit kann der Body bewegt werden.
     * @param velocityX
     * @param velocityY
     */
    public void setLinearVelocity(double velocityX, double velocityY) {
        this.velocity.set(velocityX, velocityY);
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    /**
     * Die Methode überprüft, ob das Body mit dem Body {@code other} kollidiert ist.
     * @param other Das andere Body, was überprüft werden soll, ob es eine Kollision mit dem Body detected hat.
     * @return {@code true}, wenn das Body {@code other} mit dem Body kollidiert ist.
     */
    public boolean collides(Collider other) {
        if (other == null) {
            this.logger.error("Failed to check collision because parameter \"other\" was null.");
            return false;
        }
        boolean handle = this.handleCollision(other);
        boolean check = this.wasColliding.getOrDefault(other.getId(), false);
        if (handle && !check) {
            var event = new ColliderCollisionEvent(this, other, ColliderCollisionEvent.CollisionState.COLLISION_BEGIN_CONTACT);
            this.wasColliding.put(other.getId(), true);
            if (this.onCollision != null) this.onCollision.forEach(c -> c.accept(event));
            Wrapper.getEventManager().dispatchEvent(event);

        } else if (!handle && check) {
            var event = new ColliderCollisionEvent(this, other, ColliderCollisionEvent.CollisionState.COLLISION_END_CONTACT);
            this.wasColliding.put(other.getId(), false);
            if (this.onCollision != null) this.onCollision.forEach(c -> c.accept(event));
            Wrapper.getEventManager().dispatchEvent(event);

        } else if (handle && check) {
            var event = new ColliderCollisionEvent(this, other, ColliderCollisionEvent.CollisionState.COLLISION_NORMAL_CONTACT);
            if (this.onCollision != null) this.onCollision.forEach(c -> c.accept(event));
            Wrapper.getEventManager().dispatchEvent(event);
        }
        return handle;
    }

    public abstract boolean handleCollision(Collider other);
    public abstract void drawHitbox(DrawTool drawTool);
    public abstract Vec2 getCenter();
    public abstract Interval project(Vec2 vector);
    public abstract List<Vec2> getAxes();
    public abstract AABB computeAABB();
    protected abstract ChildCollider createFootColider();

    public void addChild(Collider collider) {
        if (collider.getType() == BodyType.DYNAMIC) {
            collider.parent = this;
            this.children.add(collider.getChildInstance());

        } else {
            this.logger.info("Collider {} could not be added as child because collider is not dynamic", collider.getId());
        }
    }

    public void removeChild(ChildCollider collider) {
        if (collider != null) {
            collider.getCollider().parent = null;
            this.children.remove(collider);
        }
    }

    public boolean isGrounded() {
        return this.grounded;
    }

    public boolean isGravityEnabled() {
        return this.gravityEnabled;
    }

    public ChildCollider getChildInstance() {
        return this.childInstance;
    }

    public List<ChildCollider> getChildren() {
        return this.children;
    }

    public Collider getParent() {
        return this.parent;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }

    public void setColliderClass(String colliderClass) {
        this.colliderClass = colliderClass;
    }

    public void destroy() {
        Wrapper.getColliderManager().destroyBody(this);
        this.destroyed = true;
        this.onDestroy.forEach(c -> c.accept(new ColliderDestroyEvent(this)));
    }

    public void onCollision(Consumer<ColliderCollisionEvent> onCollision) {
        this.onCollision.add(onCollision);
    }

    public void onMove(Consumer<Collider> onMove) {
        this.onMove.add(onMove);
    }

    public void onDestroy(Consumer<ColliderDestroyEvent> onDestroy) {
        this.onDestroy.add(onDestroy);
    }

    public void update(double dt) {
        if (this.type == BodyType.DYNAMIC && this.velocity.magnitude() > 0 && this.parent == null) {
            this.move(dt);
            this.onMove.forEach(c -> c.accept(this));
            this.children.forEach(child -> {
                child.getCollider().setPosition(this.x + child.getOffsetX(), this.y + child.getOffsetY());
            });
        }
        this.moveEntity();
    }

    public void move(double dt) {
        this.x += this.velocity.x * dt;
        this.y += this.velocity.y * dt;
    }

    protected void moveEntity() {
        if (this.entity != null) {
            this.entity.setX(this.x + this.entity.getBodyOffsetX());
            this.entity.setY(this.y + this.entity.getBodyOffsetY());
        }
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.moveEntity();
        this.onMove.forEach(c -> c.accept(this));
        this.children.forEach(child -> {
            child.getCollider().setPosition(this.x + child.getOffsetX(), this.y + child.getOffsetY());
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collider body = (Collider) o;
        return this.id.equals(body.id);
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean isSensor() {
        return this.sensor;
    }

    public String getColliderClass() {
        return this.colliderClass;
    }

    public String getId() {
        return this.id;
    }

    public BodyType getType() {
        return this.type;
    }

    public void setType(BodyType type) {
        this.type = type;
    }

    public ColliderForm getForm() {
        return this.form;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public double getRadius() {
        return this.radius;
    }

    public Vec2 getVelocity() {
        return this.velocity;
    }

    public Color getHitboxColor() {
        return this.hitboxColor;
    }

    public void setHitboxColor(Color hitboxColor) {
        this.hitboxColor = hitboxColor;
    }
}
