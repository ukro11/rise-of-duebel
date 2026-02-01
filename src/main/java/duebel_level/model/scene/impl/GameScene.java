package duebel_level.model.scene.impl;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import duebel_level.Wrapper;
import duebel_level.dyn4j.PhysicsRenderer;
import duebel_level.graphics.OrderRenderer;
import duebel_level.graphics.camera.CameraRenderer;
import duebel_level.model.scene.Scene;
import duebel_level.model.sound.SoundManager;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameScene extends Scene {

    private CameraRenderer cameraRenderer;
    private List<Interactable> interactables;
    private List<Drawable> drawables;
    private OrderRenderer renderer;
    private Color background = Color.decode("#4d222c");
    private boolean drawHitboxes;

    private static GameScene instance = new GameScene();

    public static GameScene getInstance() {
        return GameScene.instance;
    }

    private GameScene() {
        super("game");
        this.drawables = new ArrayList<>();
        this.interactables = new ArrayList<>();
        this.cameraRenderer = CameraRenderer
                .create(0, 0)
                .zoom(2)
                .offset(new Vector2(0, -150))
                .smooth();
        this.renderer = new OrderRenderer();
        this.drawHitboxes = false;
    }

    @Override
    public void update(double dt) {
        this.getDrawables().forEach(d -> d.update(dt));
        if (this.currentGui == null || !this.currentGui.pauseGame()) {
            this.cameraRenderer.update(dt);
            Wrapper.getEntityManager().getEntities().forEach(e -> e.update(dt));
            Wrapper.getTooltipManager().update(dt);
            Wrapper.getLevelManager().update(dt);
        }
        super.update(dt);
    }

    public void setBackground(Color color) {
        this.background = color;
    }

    private void drawAllHitboxes(DrawTool drawTool) {
        if (this.drawHitboxes) {
            Wrapper.getEntityManager().getWorld().getBodies().forEach(b -> {
                b.getFixtures().forEach(f -> {
                    PhysicsRenderer.render(drawTool.getGraphics2D(), f.getShape(), 1.0, Color.RED);
                });
            });
        }
    }

    public void drawGame(DrawTool drawTool) {
        this.cameraRenderer.attach(drawTool);

        drawTool.setCurrentColor(this.background);
        drawTool.drawFilledRectangle(-2000, -1000, 4000, 2000);
        drawTool.resetColor();

        Wrapper.getLevelManager().draw(drawTool);

        GameScene.getInstance().getRenderer().draw(drawTool);
        this.getDrawables().forEach(d -> d.draw(drawTool));

        this.drawAllHitboxes(drawTool);

        Wrapper.getLevelManager().drawAfterPlayer(drawTool);

        this.cameraRenderer.detach(drawTool);
    }

    @Override
    public void draw(DrawTool drawTool) {
        GameScene.getInstance().drawGame(drawTool);
        Wrapper.getTooltipManager().draw(drawTool);
        Wrapper.getLevelManager().drawTransition(drawTool);
        super.draw(drawTool);
        drawTool.resetColor();
    }

    public void setDrawHitboxes(boolean hitboxes) {
        this.drawHitboxes = hitboxes;
    }

    public boolean shouldDrawHitboxes() {
        return drawHitboxes;
    }

    @Override
    public void onOpen(Scene scene) {
        Wrapper.getSoundConstants().SOUND_BACKGROUND.setVolume(0.6);
    }

    @Override
    public void onClose(Scene scene) {
        SoundManager.stopSound(Wrapper.getSoundConstants().SOUND_BACKGROUND);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        Wrapper.getEntityManager().getEntities().forEach(entity -> entity.mouseClicked(e));
        this.interactables.forEach(entity -> entity.mouseClicked(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        Wrapper.getEntityManager().getEntities().forEach(entity -> entity.mouseReleased(e));
        this.interactables.forEach(entity -> entity.mouseReleased(e));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        Wrapper.getEntityManager().getEntities().forEach(entity -> entity.mouseMoved(e));
        this.interactables.forEach(entity -> entity.mouseMoved(e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        Wrapper.getEntityManager().getEntities().forEach(entity -> entity.mouseDragged(e));
        this.interactables.forEach(entity -> entity.mouseDragged(e));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        Wrapper.getEntityManager().getEntities().forEach(entity -> entity.keyPressed(e));
        this.interactables.forEach(entity -> entity.keyPressed(e));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        Wrapper.getEntityManager().getEntities().forEach(entity -> entity.keyReleased(e));
        this.interactables.forEach(entity -> entity.keyReleased(e));
    }

    public CameraRenderer getCameraRenderer() {
        return this.cameraRenderer;
    }

    public List<Drawable> getDrawables() {
        return this.drawables;
    }

    public List<Interactable> getInteractables() {
        return this.interactables;
    }

    public OrderRenderer getRenderer() {
        return this.renderer;
    }
}
