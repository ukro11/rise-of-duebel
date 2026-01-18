package rise_of_duebel.model.scene.impl;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import rise_of_duebel.Config;
import rise_of_duebel.model.debug.VisualConstants;
import rise_of_duebel.model.scene.Scene;
import rise_of_duebel.model.transitions.DefaultTransition;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class WinScene extends Scene {

    private final List<Drawable> drawables;
    private final List<Interactable> interactables;

    public WinScene() {
        super("win");
        this.drawables = new ArrayList<>();
        this.interactables = new ArrayList<>();
    }

    @Override
    public void update(double dt) {}

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.setCurrentColor(new Color(238, 195, 154));
        drawTool.drawFilledRectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        drawTool.setCurrentColor(VisualConstants.TEXT_COLOR);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 100));

        drawTool.drawCenteredText("Gewonnen!!!".toUpperCase(), 0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            Scene.open(new LoadingScene(), new DefaultTransition());
        }
    }


}