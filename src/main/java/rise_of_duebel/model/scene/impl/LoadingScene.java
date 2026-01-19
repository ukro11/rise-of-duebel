package rise_of_duebel.model.scene.impl;

import KAGO_framework.view.DrawTool;
import rise_of_duebel.Config;
import rise_of_duebel.model.debug.VisualConstants;
import rise_of_duebel.model.scene.Scene;
import rise_of_duebel.model.transitions.DefaultTransition;

import java.awt.*;

public class LoadingScene extends Scene {

    private double loadingProgress = 0;
    private double elapsed = 0;
    private double duration = 5;
    private boolean loadingComplete = false;

    public LoadingScene() {
        super("loading");
    }

    @Override
    public void update(double dt) {
        if (this.elapsed < this.duration) {
            this.elapsed += dt;
            this.loadingProgress = this.elapsed / this.duration;
            if (this.elapsed >= this.duration) {
                this.loadingComplete = true;
            }
        }
    }

    @Override
    public void draw(DrawTool drawTool) {
        // Hintergrund
        drawTool.setCurrentColor(new Color(238, 195, 154));
        drawTool.drawFilledRectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // Titel (zentriert)
        drawTool.setCurrentColor(VisualConstants.TEXT_COLOR);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 100));
        String title = "DÜBEL LEVEL";

        drawTool.drawCenteredText("DÜBEL".toUpperCase(), 0, -130, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        drawTool.drawCenteredText("LEVEL".toUpperCase(), 0, -50, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // Ladebalken
        int barWidth = 400;
        int barHeight = 20;
        int barX = Config.WINDOW_WIDTH / 2 - barWidth / 2;
        int barY = Config.WINDOW_HEIGHT - 150;
        int progressWidth = (int) (loadingProgress * barWidth);

        // Balkenhintergrund
        drawTool.setCurrentColor(Color.DARK_GRAY);
        drawTool.drawFilledRectangle(barX, barY, barWidth, barHeight);

        // Ladefortschritt
        drawTool.setCurrentColor(Color.ORANGE);
        drawTool.drawFilledRectangle(barX, barY, progressWidth, barHeight);

        // Prozentanzeige
        drawTool.setCurrentColor(Color.BLACK);
        drawTool.getGraphics2D().setFont(VisualConstants.getFont(VisualConstants.Fonts.PIXEL_FONT, 16));
        String percentText = (int) (loadingProgress * 100) + "%";
        drawTool.drawText(percentText, barX + barWidth / 2 - 15, barY - 10);

        // Andere Elemente
        super.draw(drawTool);

        if (loadingComplete) {
            Scene.open(GameScene.getInstance(), new DefaultTransition());
        }
    }

    @Override
    public void onOpen(Scene last) {}

    public boolean isLoadingComplete() {
        return loadingComplete;
    }
}