package duebel_level.graphics.gui.impl;

import duebel_level.Wrapper;
import duebel_level.graphics.gui.Gui;
import duebel_level.graphics.gui.GuiElementPosition;
import duebel_level.graphics.gui.GuiSizing;
import duebel_level.graphics.gui.elements.GuiButton;
import duebel_level.graphics.gui.elements.GuiToggle;
import duebel_level.model.debug.VisualModel;
import duebel_level.model.scene.Scene;
import duebel_level.model.scene.impl.GameScene;
import org.dyn4j.geometry.Vector2;

import java.awt.event.KeyEvent;

/***
 * @author Leon
 */
public class GuiPause extends Gui {

    public GuiPause() {
        super(600, 340);
        GuiButton resumeBtn = new GuiButton("RESUME", GuiSizing.of(GuiElementPosition.CENTER, GuiElementPosition.START, 400, 60), 30);
        resumeBtn.setOffset(new Vector2(0, 60));
        resumeBtn.onClick(btn -> Scene.getCurrentScene().closeGUI());
        this.addElement(resumeBtn);

        GuiToggle fpsBtn = new GuiToggle("SHOW  FPS", false, GuiSizing.of(GuiElementPosition.CENTER, GuiElementPosition.START, 400, 60), 30);
        fpsBtn.setOffset(new Vector2(0, 60 * 2 + 20));
        fpsBtn.onValueChange(v -> {
            VisualModel vis = GameScene.getInstance().getVisual("fps-component");
            if (vis != null) vis.toggleVisible(v);
        });
        this.addElement(fpsBtn);

        GuiButton exitBtn = new GuiButton("EXIT", GuiSizing.of(GuiElementPosition.CENTER, GuiElementPosition.START, 400, 60), 30);
        exitBtn.setOffset(new Vector2(0, 60 * 3 + 20 * 2));
        exitBtn.onClick(btn -> Wrapper.getViewController().shutdown());
        this.addElement(exitBtn);
    }

    @Override
    public boolean shouldOpen() {
        return Scene.getCurrentScene() == GameScene.getInstance() && !Scene.performingTransition() && !Wrapper.getLevelManager().performingTransition();
    }

    @Override
    public int keyToOpen() {
        return KeyEvent.VK_ESCAPE;
    }
}
