package rise_of_duebel.model.scene.impl;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.view.DrawTool;
import rise_of_duebel.Config;
import rise_of_duebel.model.KeyManagerModel;
import rise_of_duebel.model.debug.VisualConstants;
import rise_of_duebel.model.scene.Scene;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class StartScene extends Scene {

    private final List<Drawable> drawables;
    private final List<Interactable> interactables;

    public StartScene() {
        super("start");
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
        drawTool.setCurrentColor(VisualConstants.TEXT_COLOR);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 100));

        drawTool.drawCenteredText("Rise".toUpperCase(), 0, -130, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        drawTool.drawCenteredText("of".toUpperCase(), 0, -50, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        drawTool.drawCenteredText("Dübel".toUpperCase(), 0, 20, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        int keyX = Config.WINDOW_WIDTH / 2 - 210 + KeyManagerModel.KEY_START_GAME.getIcon().getWidth() / 2;
        int keyY = Config.WINDOW_HEIGHT - 210;

        drawTool.getGraphics2D().drawImage(KeyManagerModel.KEY_START_GAME.getIcon(), keyX, keyY, 40, 40, null);

        drawTool.setCurrentColor(Color.BLACK);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 18));
        drawTool.drawCenteredText(KeyManagerModel.KEY_START_GAME.getDescription().replace(" ", "  ").toUpperCase(), KeyManagerModel.KEY_START_GAME.getIcon().getWidth() / 2, (keyY - Config.WINDOW_HEIGHT / 2) + 20, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
    }
   // @Override
   // public void keyPressed(KeyEvent e) {
       // if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          //  Scene.open(GameScene.getInstance());
       // }
   // }


}