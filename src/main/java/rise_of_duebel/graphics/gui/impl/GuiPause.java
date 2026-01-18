package rise_of_duebel.graphics.gui.impl;

import org.dyn4j.geometry.Vector2;
import rise_of_duebel.Wrapper;
import rise_of_duebel.graphics.gui.Gui;
import rise_of_duebel.graphics.gui.GuiElementPosition;
import rise_of_duebel.graphics.gui.GuiSizing;
import rise_of_duebel.graphics.gui.elements.GuiButton;
import rise_of_duebel.graphics.gui.elements.GuiToggle;
import rise_of_duebel.model.debug.VisualModel;
import rise_of_duebel.model.scene.Scene;
import rise_of_duebel.model.scene.impl.GameScene;

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
        return Scene.getCurrentScene() == GameScene.getInstance();
    }

    @Override
    public int keyToOpen() {
        return KeyEvent.VK_ESCAPE;
    }
}
