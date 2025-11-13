package rise_of_duebel.physics.colliders;

import KAGO_framework.view.DrawTool;
import rise_of_duebel.Wrapper;
import rise_of_duebel.physics.*;
import rise_of_duebel.utils.Vec2;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColliderPolygon extends Collider {

    public Vec2[] vertices;
    public Vec2[] normals;
    private Vec2 center;

    public static ColliderPolygon createCIPolygon(String id, BodyType type, double x, double y, double width, double height, double curvedInFactor) {
        double offsetY = 0;
        var vertices = new Vec2[] {
            new Vec2(-width/2, -height/2 + curvedInFactor - offsetY), new Vec2(-width/2 + curvedInFactor, -height/2 - offsetY),
            new Vec2(width/2 - curvedInFactor, -height/2 - offsetY), new Vec2(width/2, -height/2 + curvedInFactor - offsetY),
            new Vec2(width/2, height/2 - curvedInFactor - offsetY), new Vec2(width/2 - curvedInFactor, height/2 - offsetY),
            new Vec2(-width/2 + curvedInFactor, height/2 - offsetY), new Vec2(-width/2, height/2 - curvedInFactor - offsetY)
        };
        return new ColliderPolygon(id, type, x, y, vertices);
    }

    public ColliderPolygon(BodyType type, double x, double y, Vec2[] vertices) {
        this(UUID.randomUUID().toString(), type, x, y, vertices);
    }

    public ColliderPolygon(String id, BodyType type, double x, double y, Vec2[] vertices) {
        this(id, type, x, y, vertices, true);
    }

    public ColliderPolygon(String id, BodyType type, double x, double y, Vec2[] vertices, boolean register) {
        this.id = id;
        this.type = type;
        if (vertices.length <= 2) {
            throw new InvalidParameterException("Polygons have atleast 3 vertices");
        }
        this.x = x;
        this.y = y;
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].add(x, y);
        }
        this.vertices = vertices;
        this.form = ColliderForm.POLYGON;
        this.colliderClass = "default";
        this.normals = this.getCounterClockwiseEdgeNormals(this.vertices);
        this.computeCenter();

        if (register) Wrapper.getColliderManager().createBody(this);
    }

    @Override
    public boolean handleCollision(Collider other) {
        if (this.isDestroyed()) {
            return false;
        }

        if (!AABB.compute(this, other)) {
            return false;
        }

        return SAT.compute(this, other);
    }

    @Override
    public AABB computeAABB() {
        Vec2 p = this.vertices[0];
        double minX = p.x;
        double maxX = p.x;
        double minY = p.y;
        double maxY = p.y;
        int size = this.vertices.length;
        for(int i = 1; i < size; i++) {
            double px = this.vertices[i].x;
            double py = this.vertices[i].y;
            if (px < minX) {
                minX = px;
            } else if (px > maxX) {
                maxX = px;
            }
            if (py < minY) {
                minY = py;
            } else if (py > maxY) {
                maxY = py;
            }
        }
        return new AABB(minX, minY, maxX, maxY);
    }

    @Override
    public void move(double dt) {
        if (this.vertices != null && !this.isDestroyed() && this.velocity.magnitude() > 0) {
            for (int i = 0; i < this.vertices.length; i++) {
                this.vertices[i].add(this.velocity.x * dt, this.velocity.y * dt);
            }
            this.x += this.velocity.x * dt;
            this.y += this.velocity.y * dt;
            this.center.add(this.velocity.x * dt, this.velocity.y * dt);
        }
    }

    @Override
    public void setPosition(double x, double y) {
        for (int i = 0; i < this.vertices.length; i++) {
            var v = this.vertices[i];
            double relX = v.x - this.x;
            double relY = v.y - this.y;
            v.set(x + relX, y + relY);
        }
        super.setPosition(x, y);
        double relX = this.center.x - this.x;
        double relY = this.center.y - this.y;
        this.center.add(x + relX, y + relY);
    }

    @Override
    public void drawHitbox(DrawTool drawTool) {
        if (this.vertices != null && !this.isDestroyed()) {
            drawTool.setCurrentColor(this.hitboxColor);
            drawTool.drawFilledCircle(this.center.x, this.center.y, 1);
            for (int i = 0; i < this.vertices.length; i++) {
                drawTool.drawLine(this.vertices[i].x, this.vertices[i].y, this.vertices[(i + 1) % this.vertices.length].x, this.vertices[(i + 1) % this.vertices.length].y);
            }
            drawTool.resetColor();
        }
    }

    private Vec2[] getCounterClockwiseEdgeNormals(Vec2... vertices) {
        if (vertices == null) return null;

        int size = vertices.length;
        if (size == 0) return null;

        Vec2[] normals = new Vec2[size];
        for (int i = 0; i < size; i++) {
            Vec2 p1 = vertices[i];
            Vec2 p2 = vertices[(i + 1) % size];
            Vec2 n = p2.clone().sub(p1).left();
            n.normalize();
            normals[i] = n;
        }

        return normals;
    }

    @Override
    public List<Vec2> getAxes() {
        List<Vec2> axes = new ArrayList<>();
        for (int i = 0; i < this.vertices.length; i++) {
            Vec2 v = this.normals[i];
            axes.add(v);
        }
        return axes;
    }

    @Override
    public Interval project(Vec2 vector) {
        if (this.vertices == null) return null;

        double v = 0.0;
        Vec2 p = this.vertices[0];
        double min = vector.dot(p);
        double max = min;
        int size = this.vertices.length;
        for(int i = 1; i < size; i++) {
            p = this.vertices[i];
            v = vector.dot(p);
            if (v < min) {
                min = v;
            } else if (v > max) {
                max = v;
            }
        }
        return new Interval(min, max);
    }

    private void computeCenter() {
        if (this.isDestroyed() || this.vertices == null) return;

        Vec2 ac = this.getAverageCenter(this.vertices);
        int size = this.vertices.length;

        Vec2 center = new Vec2();
        double area = 0.0;
        for (int i = 0; i < size; i++) {
            Vec2 p1 = this.vertices[i].clone();
            Vec2 p2 = i + 1 < size ? this.vertices[i + 1].clone() : this.vertices[0].clone();
            p1 = p1.sub(ac);
            p2 = p2.sub(ac);
            double d = p1.cross(p2);
            double triangleArea = 0.5 * d;
            area += triangleArea;

            // area weighted centroid
            // (p1 + p2) * (D / 3)
            // = (x1 + x2) * (yi * x(i+1) - y(i+1) * xi) / 3
            center.add(p1.add(p2).mul(1.0 / 3.0).mul(triangleArea));
        }
        if (Math.abs(area) <= Epsilon.E) {
            // zero area can only happen if all the points are the same point
            this.center = this.vertices[0].clone();
            return;
        }
        center.div(area).add(ac);
        this.center = center;
    }

    @Override
    public Vec2 getCenter() {
        return this.center;
    }

    public Vec2 getAverageCenter(Vec2[] points) {
        points = points.clone();
        if (points == null)
            throw new NullPointerException("points");

        if (points.length == 0)
            throw new NullPointerException("points");

        int size = points.length;
        if (size == 1) {
            Vec2 p = points[0];
            if (p == null)
                throw new NullPointerException("points");
            return p.clone();
        }

        Vec2 ac = new Vec2();
        for (int i = 0; i < size; i++) {
            Vec2 point = points[i];
            if (point == null)
                throw new NullPointerException("points");
            ac.add(point);
        }

        return ac.div(size);
    }
}
