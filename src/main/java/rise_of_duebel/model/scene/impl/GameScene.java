package rise_of_duebel.model.scene.impl;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import org.dyn4j.geometry.Vector2;
import rise_of_duebel.Wrapper;
import rise_of_duebel.graphics.OrderRenderer;
import rise_of_duebel.graphics.camera.CameraRenderer;
import rise_of_duebel.graphics.level.spawner.ObjectSpawner;
import rise_of_duebel.model.scene.Scene;
import rise_of_duebel.model.sound.SoundManager;

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
    }

    @Override
    public void update(double dt) {
        this.cameraRenderer.update(dt);
        Wrapper.getEntityManager().getEntities().values().forEach(e -> e.update(dt));
        this.getDrawables().forEach(d -> d.update(dt));
        for (ObjectSpawner<?> spawner : ObjectSpawner.objects) {
            spawner.update(dt);
        }
        Wrapper.getTooltipManager().update(dt);
        Wrapper.getLevelManager().update(dt);
        super.update(dt);
    }

    public void setBackground(Color color) {
        this.background = color;
    }

    private void drawAllHitboxes(DrawTool drawTool) {}

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
        //drawTool.setCurrentColor(new Color(157, 196, 94), 50);
        //drawTool.drawFilledRectangle(0, 0, 4000, 4000);
        drawTool.resetColor();
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
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseClicked(e));
        this.interactables.forEach(entity -> entity.mouseClicked(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseReleased(e));
        this.interactables.forEach(entity -> entity.mouseReleased(e));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseMoved(e));
        this.interactables.forEach(entity -> entity.mouseMoved(e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.mouseDragged(e));
        this.interactables.forEach(entity -> entity.mouseDragged(e));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.keyPressed(e));
        this.interactables.forEach(entity -> entity.keyPressed(e));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        Wrapper.getEntityManager().getEntities().values().forEach(entity -> entity.keyReleased(e));
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
