package rise_of_duebel.model.entity;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import org.dyn4j.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.ProgramController;
import rise_of_duebel.animation.AnimationRenderer;
import rise_of_duebel.animation.IAnimationState;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.graphics.IOrderRenderer;
import rise_of_duebel.model.scene.impl.GameScene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;

public abstract class Entity<T extends Enum<T> & IAnimationState> implements Drawable, Interactable, IOrderRenderer {

    protected ViewController viewController;
    protected ProgramController programController;
    protected final Logger logger = LoggerFactory.getLogger(Entity.class);

    protected String id;
    protected final ColliderBody body;
    protected final World<ColliderBody> world;
    private double x;
    private double y;
    protected double width;
    protected double height;
    protected boolean showHitbox;

    protected AnimationRenderer<T> renderer;

    public Entity(World<ColliderBody> world, ColliderBody body, double x, double y, double width, double height) {
        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();

        this.world = world;
        this.body = body;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.showHitbox = false;

        GameScene.getInstance().getRenderer().register(this);
    }

    @Override
    public void draw(DrawTool drawTool) {
        this.drawEntity(drawTool);
        this.drawHitbox(drawTool);
    }

    protected void drawEntity(DrawTool drawTool) {
        if (this.renderer != null && this.renderer.getCurrentFrame() != null) {
            drawTool.push();
            drawTool.getGraphics2D().drawImage(this.renderer.getCurrentFrame(), (int) this.getX(), (int) this.getY(), (int) this.width, (int) this.height, null);
            drawTool.pop();
        }
    }

    protected void drawHitbox(DrawTool drawTool) {
        if (this.showHitbox && this.body != null) {
            drawTool.setCurrentColor(this.getBody().getColor());
            this.body.render(drawTool, 1);
        }
    }

    @Override
    public void update(double dt) {
        if (this.renderer != null) {
            if (!this.renderer.isRunning()) this.renderer.start();
            this.renderer.update(dt);
        }
    }

    @Override
    public double zIndex() {
        return this.y;
    }

    public AnimationRenderer<T> getRenderer() {
        return this.renderer;
    }

    public void setRenderer(AnimationRenderer<T> renderer) {
        this.renderer = renderer;
    }

    public boolean shouldShowHitbox() {
        return this.showHitbox;
    }

    public void setShowHitbox(boolean showHitbox) {
        this.showHitbox = showHitbox;
    }

    public ColliderBody getBody() {
        return body;
    }

    public String getId() {
        return this.id;
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

    @Override
    public void keyPressed(KeyEvent key) {}

    @Override
    public void keyReleased(KeyEvent key) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
