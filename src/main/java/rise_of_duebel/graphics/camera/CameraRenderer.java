package rise_of_duebel.graphics.camera;

import KAGO_framework.model.abitur.datenstrukturen.Queue;
import KAGO_framework.view.DrawTool;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;
import rise_of_duebel.Config;
import rise_of_duebel.Wrapper;
import rise_of_duebel.event.events.CameraMoveEvent;
import rise_of_duebel.model.entity.Entity;
import rise_of_duebel.model.scene.impl.GameScene;
import rise_of_duebel.utils.MathUtils;

import java.awt.*;

public class CameraRenderer {

    private Vector2 pos = new Vector2();
    private boolean smooth = false;
    private double zoom = 1.0;
    private double angle = 0;
    private double angleOffset = 0;
    private Vector2 offset = new Vector2();
    private Vector2 prevScale;
    private Vector2 cameraMax = new Vector2(10000, 10000);

    private Entity focusEntity;
    private Vector2 focusPoint;

    private Queue<CameraEffect> effectQueue;

    private CameraRenderer(double startX, double startY) {
        this.pos.set(startX, startY);
        this.effectQueue = new Queue<>();
    }

    public static CameraRenderer create(double startX, double startY) {
        return new CameraRenderer(startX, startY);
    }

    public CameraRenderer smooth() {
        this.smooth = true;
        return this;
    }

    public CameraRenderer zoom(double zoom) {
        this.zoom = zoom;
        return this;
    }

    public CameraRenderer angle(double angle) {
        this.angle = angle;
        return this;
    }

    public CameraRenderer offset(Vector2 offset) {
        this.offset = offset;
        return this;
    }

    public void shake(CameraShake shake) {
        this.effectQueue.enqueue(shake);
    }

    public void attach(DrawTool drawTool) {
        if (this.prevScale == null) {
            this.prevScale = new Vector2();
            this.prevScale.set(drawTool.getGraphics2D().getTransform().getScaleX(), drawTool.getGraphics2D().getTransform().getScaleY());
        }
        drawTool.push();
        drawTool.getGraphics2D().translate(-Math.floor(this.pos.x), -Math.floor(this.pos.y));
        drawTool.getGraphics2D().scale(this.zoom, this.zoom);
        drawTool.getGraphics2D().rotate(Math.toRadians(this.angle + this.angleOffset));
    }

    public void detach(DrawTool drawTool) {
        drawTool.getGraphics2D().scale(this.prevScale.x, this.prevScale.y);
        drawTool.pop();
    }

    public void centerLines(DrawTool drawTool) {
        drawTool.setCurrentColor(Color.RED);
        drawTool.drawLine(0, Config.WINDOW_HEIGHT / 2, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT / 2);
        drawTool.drawLine(Config.WINDOW_WIDTH / 2, 0, Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT);
        drawTool.resetColor();
    }

    private double smoothDamp(double current, double target, double currentVelocity, double smoothTime, double maxSpeed, double deltaTime) {
        smoothTime = Math.max(0.0001, smoothTime);
        double direction = target - current;
        double distance = Math.abs(direction);
        if (distance < 0.1) {
            return target;
        }
        double maxVelocity = maxSpeed * smoothTime;
        direction = MathUtils.clamp(direction, -maxVelocity, maxVelocity);
        double targetVelocity = direction / smoothTime;
        double newVelocity = currentVelocity + (targetVelocity - currentVelocity) * deltaTime * 5;
        double newPosition = current + newVelocity * deltaTime;
        if ((target > current && newPosition > target) ||
                (target < current && newPosition < target)) {
            newPosition = target;
        }
        Wrapper.getEventManager().dispatchEvent(new CameraMoveEvent(this));
        return newPosition;
    }

    public void focusNoLimit(double x, double y, double dt) {
        double camX = x * this.zoom - Config.WINDOW_WIDTH / 2 + this.offset.x;
        double camY = y * this.zoom - Config.WINDOW_HEIGHT / 2 + this.offset.y;
        double diffX = camX - this.pos.x;
        double diffY = camY - this.pos.y;
        double tempX = this.pos.x + diffX;
        double tempY = this.pos.y + diffY;
        this.pos.x = tempX;
        this.pos.y = tempY;
    }

    private void focus(double x, double y, double dt) {
        double camX = x * this.zoom - Config.WINDOW_WIDTH / 2 + this.offset.x;
        double camY = y * this.zoom - Config.WINDOW_HEIGHT / 2 + this.offset.y;
        double diffX = camX - this.pos.x;
        double diffY = camY - this.pos.y;
        double tempX = this.pos.x + diffX;
        double tempY = this.pos.y + diffY;

        CameraEffect effect = this.effectQueue.front();
        Vector3 effectVec = null;
        if (effect != null) {
            effectVec = effect.initiate(this, dt);
            this.angleOffset = effectVec.z;
            if (effect.isFinished()) this.effectQueue.dequeue();
        }

        this.pos.x = MathUtils.clamp(tempX, 0, this.cameraMax.x * this.zoom) + effectVec.x;
        this.pos.y = MathUtils.clamp(tempY, 0, this.cameraMax.y * this.zoom) + effectVec.y;
    }

    private void focusSmooth(double x, double y, double dt) {
        double camX = x * this.zoom - Config.WINDOW_WIDTH / 2 + this.offset.x;
        double camY = y * this.zoom - Config.WINDOW_HEIGHT / 2 + this.offset.y;
        Vector2 velocity = new Vector2(camX - this.pos.x, camY - this.pos.y);

        CameraEffect effect = this.effectQueue.front();
        Vector3 effectVec = new Vector3();
        if (effect != null) {
            effectVec = effect.initiate(this, dt);
            this.angleOffset = effectVec.z;
            if (effect.isFinished()) this.effectQueue.dequeue();
        }

        this.pos.x = smoothDamp(this.pos.x, camX, velocity.x, 0.1, 100_000, dt * 5) + effectVec.x;
        this.pos.y = smoothDamp(this.pos.y, camY, velocity.y, 0.1, 100_000, dt * 5) + effectVec.y;
    }

    public void focusAt(double x, double y) {
        this.focusPoint = new Vector2(x, y);
    }

    public void focusAtEntity(Entity entity) {
        this.focusEntity = entity;
    }

    public void update(double dt) {
        Vector2 pos = null;
        if (this.focusEntity != null) pos = this.focusEntity.getBody().getPosition();
        else if (this.focusPoint != null) pos = this.focusPoint;
        else pos = new Vector2();

        if (this.smooth) {
            this.focusSmooth(pos.x, pos.y, dt);

        } else {
            this.focus(pos.x, pos.y, dt);
        }
    }

    public final Vector2 toWorldCoordinates(double width, double height, Point p) {
        if (p != null) {
            Vector2 v = new Vector2();
            // convert the screen space point to world space
            v.x =  (p.getX() - width * 0.5 - this.offset.x) / this.zoom;
            v.y = -(p.getY() - height * 0.5 + this.offset.y) / this.zoom;
            return v;
        }

        return null;
    }

    public double getX() {
        return this.pos.x;
    }

    public double getY() {
        return this.pos.y;
    }

    public Vector2 getPosition() {
        return this.pos;
    }

    public double getWorldX() {
        return Math.floor(GameScene.getInstance().getCameraRenderer().getX() / GameScene.getInstance().getCameraRenderer().getZoom());
    }

    public double getWorldY() {
        return Math.floor(GameScene.getInstance().getCameraRenderer().getY() / GameScene.getInstance().getCameraRenderer().getZoom());
    }

    public double getZoom() {
        return this.zoom;
    }
}
