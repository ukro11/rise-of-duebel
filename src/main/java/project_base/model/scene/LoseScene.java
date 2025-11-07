package project_base.model.scene;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import project_base.Config;
import project_base.model.debug.VisualConstants;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class LoseScene extends Scene {

    private final List<Drawable> drawables;
    private final List<Interactable> interactables;

    public LoseScene() {
        super("lose");
        this.drawables = new ArrayList<>();
        this.interactables = new ArrayList<>();
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw(DrawTool drawTool) {
        // Hintergrund
        drawTool.setCurrentColor(new Color(238, 195, 154));
        drawTool.drawFilledRectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // Titel (zentriert)
        drawTool.setCurrentColor(new Color(104, 17, 17));
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 100));

        drawTool.drawCenteredText("Game".toUpperCase(), 0, -80, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        drawTool.drawCenteredText("Over".toUpperCase(), 0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            Scene.open(new LoadingScene());
        }
    }


}