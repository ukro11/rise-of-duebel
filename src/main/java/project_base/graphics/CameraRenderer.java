package project_base.graphics;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import project_base.Config;
import project_base.animation.Easings;
import project_base.ProgramController;
import project_base.model.entity.Entity;
import project_base.utils.MathUtils;
import project_base.utils.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

public class CameraRenderer {

    private Logger logger = LoggerFactory.getLogger(CameraRenderer.class);
    private ViewController viewController;
    private ProgramController programController;
    private Vec2 anchor;
    private double x;
    private double y;
    private boolean smooth = false;
    private Function<Double, Double> easing = (x) -> Easings.easeInCubic(x);
    private double zoom = 1.0;
    private double angle = 0;
    private double duration = 1.0;
    private double elapsed = 0.0;
    private Vec2 offset = new Vec2();
    private Vec2 prevScale;
    private Entity focusEntity;
    private Vec2 cameraMax = new Vec2(10000, 10000);

    private Map.Entry<Instant, Double> currentShake;
    private double shakeElapsed = 0.0;
    private Map<Instant, Double> queue = new HashMap<>();

    private CameraRenderer(ViewController view, ProgramController controller, double startX, double startY) {
        this.viewController = view;
        this.programController = controller;
        this.x = startX;
        this.y = startY;
        this.anchor = new Vec2(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2);
    }

    public static CameraRenderer create(ViewController view, ProgramController controller, double startX, double startY) {
        return new CameraRenderer(view, controller, startX, startY);
    }

    public CameraRenderer smooth(Function<Double, Double> easing) {
        this.smooth = true;
        this.easing = easing;
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

    public CameraRenderer offset(Vec2 offset) {
        this.offset = offset;
        return this;
    }

    public void attach(DrawTool drawTool) {
        if (this.prevScale == null) {
            this.prevScale = new Vec2();
            this.prevScale.set(drawTool.getGraphics2D().getTransform().getScaleX(), drawTool.getGraphics2D().getTransform().getScaleY());
        }
        drawTool.push();
        drawTool.getGraphics2D().translate(-Math.floor(this.x), -Math.floor(this.y));
        drawTool.getGraphics2D().scale(this.zoom, this.zoom);
        drawTool.getGraphics2D().rotate(Math.toRadians(this.angle));
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

    public void shake(double duration) {
        this.queue.put(Instant.now(), duration);
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
        return newPosition;
    }

    public void focusNoLimit(double x, double y, double dt) {
        double camX = x * this.zoom - Config.WINDOW_WIDTH / 2 + this.offset.x;
        double camY = y * this.zoom - Config.WINDOW_HEIGHT / 2 + this.offset.y;
        double diffX = camX - this.x;
        double diffY = camY - this.y;
        double tempX = this.x + diffX;
        double tempY = this.y + diffY;
        this.x = tempX;
        this.y = tempY;
    }

    private void focus(double x, double y, double dt) {
        double camX = x * this.zoom - Config.WINDOW_WIDTH / 2 + this.offset.x;
        double camY = y * this.zoom - Config.WINDOW_HEIGHT / 2 + this.offset.y;
        double diffX = camX - this.x;
        double diffY = camY - this.y;
        double tempX = this.x + diffX;
        double tempY = this.y + diffY;
        this.x = MathUtils.clamp(tempX, 0, this.cameraMax.x * this.zoom);
        this.y = MathUtils.clamp(tempY, 0, this.cameraMax.y * this.zoom);
    }

    private void focusSmooth(double x, double y, double dt) {
        double camX = x * this.zoom - Config.WINDOW_WIDTH / 2 + this.offset.x;
        double camY = y * this.zoom - Config.WINDOW_HEIGHT / 2 + this.offset.y;
        Vec2 velocity = new Vec2(camX - this.x, camY - this.y);
        camX = MathUtils.clamp(camX, 0, this.cameraMax.x * this.zoom);
        camY = MathUtils.clamp(camY, 0, this.cameraMax.y * this.zoom);

        this.x = smoothDamp(this.x, camX, velocity.x, 0.1, 100_000, dt * 5);
        this.y = smoothDamp(this.y, camY, velocity.y, 0.1, 100_000, dt * 5);
    }

    public void focusAt(double x, double y, double dt) {
        if (this.focusEntity == null) {
            if (this.smooth) {
                this.focusSmooth(x, y, dt);

            } else {
                this.focus(x, y, dt);
            }
        }
    }

    public void focusAtEntity(Entity entity) {
        this.focusEntity = entity;
    }

    public Map.Entry<Instant, Double> get() {
        return this.queue.entrySet().stream().findFirst().get();
    }

    public void update(double dt) {
        if (this.focusEntity != null) {
            if (this.smooth) {
                this.focusSmooth(this.focusEntity.getBody().getX(), this.focusEntity.getBody().getY(), dt);

            } else {
                this.focus(this.focusEntity.getBody().getX(), this.focusEntity.getBody().getY(), dt);
            }
        }
        if (this.queue.size() > 0) {
            if (this.currentShake == null) {
                this.currentShake = this.get();
                this.shakeElapsed = this.currentShake.getValue();
            }

            this.shakeElapsed = Math.max(this.shakeElapsed - dt, 0);

            if (this.shakeElapsed == 0) {
                this.queue.remove(this.currentShake.getKey());
                this.currentShake = null;
                this.shakeElapsed = 0;

            } else {
                double shakeProgress = this.shakeElapsed / this.currentShake.getValue();

                double shakeMagnitude = Math.max(0.5, Math.min(1.5, shakeProgress));

                double shakeOffsetX = (Math.random() - 0.5) * shakeMagnitude * 10;
                double shakeOffsetY = (Math.random() - 0.5) * shakeMagnitude * 10;

                this.x = MathUtils.clamp(this.x + shakeOffsetX, 0, Config.WINDOW_WIDTH);
                this.y = MathUtils.clamp(this.y + shakeOffsetY, 0, Config.WINDOW_HEIGHT);
            }
        }
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZoom() {
        return this.zoom;
    }
}
