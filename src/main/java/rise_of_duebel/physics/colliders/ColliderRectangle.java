package rise_of_duebel.physics.colliders;

import rise_of_duebel.physics.AABB;
import rise_of_duebel.physics.BodyType;
import rise_of_duebel.physics.ColliderForm;
import rise_of_duebel.utils.Vec2;

import java.util.UUID;

public class ColliderRectangle extends ColliderPolygon {

    public ColliderRectangle(BodyType type, double x, double y, double width, double height) {
        this(UUID.randomUUID().toString(), type, x, y, width, height);
    }

    public ColliderRectangle(String id, BodyType type, double x, double y, double width, double height) {
        super(id, type, x, y, new Vec2[] { new Vec2(0, 0), new Vec2(width, 0), new Vec2(width, height), new Vec2(0, height) });
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.form = ColliderForm.RECTANGLE;
    }

    @Override
    public AABB computeAABB() {
        AABB aabb = new AABB(0, 0, 0, 0);
        double v0x = this.vertices[0].x;
        double v0y = this.vertices[0].y;
        double v1x = this.vertices[1].x;
        double v1y = this.vertices[1].y;
        double v2x = this.vertices[2].x;
        double v2y = this.vertices[2].y;
        double v3x = this.vertices[3].x;
        double v3y = this.vertices[3].y;

        if (v0y > v1y) {
            if (v0x < v1x) {
                aabb.minX = v0x;
                aabb.minY = v1y;
                aabb.maxX = v2x;
                aabb.maxY = v3y;
            } else {
                aabb.minX = v1x;
                aabb.minY = v2y;
                aabb.maxX = v3x;
                aabb.maxY = v0y;
            }
        } else {
            if (v0x < v1x) {
                aabb.minX = v3x;
                aabb.minY = v0y;
                aabb.maxX = v1x;
                aabb.maxY = v2y;
            } else {
                aabb.minX = v2x;
                aabb.minY = v3y;
                aabb.maxX = v0x;
                aabb.maxY = v1y;
            }
        }
        return aabb;
    }
}
