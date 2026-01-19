package duebel_level.model.entity;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import duebel_level.ProgramController;
import duebel_level.animation.AnimationRenderer;
import duebel_level.animation.IAnimationState;
import duebel_level.dyn4j.ColliderBody;
import duebel_level.graphics.IOrderRenderer;
import duebel_level.model.scene.impl.GameScene;
import org.dyn4j.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;

/***
 * @author Mark
 */
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

    /**
     * Erstellt eine Entity und registriert sie beim GameScene-Renderer.
     *
     * @param world Physics-World
     * @param body ColliderBody
     * @param x Start-X
     * @param y Start-Y
     * @param width Render-Breite
     * @param height Render-Höhe
     */
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

    /**
     * Zeichnet Entity und optional die Hitbox.
     *
     * @param drawTool DrawTool
     */
    @Override
    public void draw(DrawTool drawTool) {
        this.drawEntity(drawTool);
        this.drawHitbox(drawTool);
    }

    /**
     * Zeichnet den aktuellen Animationsframe (falls vorhanden).
     *
     * @param drawTool DrawTool
     */
    protected void drawEntity(DrawTool drawTool) {
        if (this.renderer != null && this.renderer.getCurrentFrame() != null) {
            drawTool.push();
            drawTool.getGraphics2D().drawImage(this.renderer.getCurrentFrame(), (int) this.getX(), (int) this.getY(), (int) this.width, (int) this.height, null);
            drawTool.pop();
        }
    }

    /**
     * Zeichnet den Physics-Body als Hitbox (wenn aktiviert).
     *
     * @param drawTool DrawTool
     */
    protected void drawHitbox(DrawTool drawTool) {
        if (this.showHitbox && this.body != null) {
            drawTool.setCurrentColor(this.getBody().getColor());
            this.body.render(drawTool, 1);
        }
    }

    /**
     * Aktualisiert den AnimationRenderer (falls vorhanden).
     *
     * @param dt delta time
     */
    @Override
    public void update(double dt) {
        if (this.renderer != null) {
            if (!this.renderer.isRunning()) this.renderer.start();
            this.renderer.update(dt);
        }
    }

    /**
     * Standard-Z-Order: y-Position (niedriger = weiter oben).
     *
     * @return zIndex
     */
    @Override
    public double zIndex() {
        return this.y;
    }

    /**
     * @return AnimationRenderer oder null
     */
    public AnimationRenderer<T> getRenderer() {
        return this.renderer;
    }

    /**
     * Setzt den AnimationRenderer.
     *
     * @param renderer Renderer
     */
    public void setRenderer(AnimationRenderer<T> renderer) {
        this.renderer = renderer;
    }

    /**
     * @return true, wenn Hitbox gezeichnet werden soll
     */
    public boolean shouldShowHitbox() {
        return this.showHitbox;
    }

    /**
     * Aktiviert/deaktiviert Hitbox-Rendering.
     *
     * @param showHitbox Flag
     */
    public void setShowHitbox(boolean showHitbox) {
        this.showHitbox = showHitbox;
    }

    /**
     * @return Physics-Body
     */
    public ColliderBody getBody() {
        return body;
    }

    /**
     * @return Entity-ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return X-Position
     */
    public double getX() {
        return this.x;
    }

    /**
     * @return Y-Position
     */
    public double getY() {
        return this.y;
    }

    /**
     * @param x neue X-Position
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @param y neue Y-Position
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return Render-Breite
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * @return Render-Höhe
     */
    public double getHeight() {
        return this.height;
    }

    /** Default: keine Key-Handling Logik. */
    @Override
    public void keyPressed(KeyEvent key) {}

    /** Default: keine Key-Handling Logik. */
    @Override
    public void keyReleased(KeyEvent key) {}

    /** Default: keine Mouse-Handling Logik. */
    @Override
    public void mouseReleased(MouseEvent e) {}

    /** Default: keine Mouse-Handling Logik. */
    @Override
    public void mouseClicked(MouseEvent e) {}

    /** Default: keine Mouse-Handling Logik. */
    @Override
    public void mouseDragged(MouseEvent e) {}

    /** Default: keine Mouse-Handling Logik. */
    @Override
    public void mouseMoved(MouseEvent e) {}

    /** Default: keine Mouse-Handling Logik. */
    @Override
    public void mousePressed(MouseEvent e) {}

    /**
     * Vergleicht Entities anhand der ID.
     *
     * @param o Vergleichsobjekt
     * @return true bei gleicher ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(this.id, entity.id);
    }
}
