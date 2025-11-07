package project_base.model.scene;

import KAGO_framework.view.DrawTool;
import project_base.Config;
import project_base.Wrapper;
import project_base.model.sound.SoundManager;
import project_base.model.debug.VisualConstants;

import java.awt.*;

public class LoadingScene extends Scene {

    private double loadingProgress = 0;  // Wert zwischen 0.0 und 1.0
    private double elapsed = 0;
    private double duration = 5;
    private boolean loadingComplete = false;

    public LoadingScene() {
        super("loading");
    }

    @Override
    public void update(double dt) {
        // Fortschritt erhöhen (max 1.0)
        if (elapsed < duration) {
            elapsed += dt;  // Geschwindigkeit anpassen (0.25 = ca. 4s)
            loadingProgress = elapsed / duration;
            if (elapsed >= duration) {
                loadingComplete = true;

                // Beispiel: Wechsle zur GameScene oder MainMenuScene
                // Wrapper.getSceneManager().changeScene(GameScene.getInstance());
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
        String title = "Kebab      Simulator";

        drawTool.drawCenteredText("Kebab".toUpperCase(), 0, -130, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        drawTool.drawCenteredText("Simulator".toUpperCase(), 0, -50, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

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
            Scene.open(new StartScene());
        }
    }

    @Override
    public void onOpen(Scene last) {
        Wrapper.getSoundConstants().SOUND_BACKGROUND.setVolume(0.5);
        SoundManager.playSound(Wrapper.getSoundConstants().SOUND_BACKGROUND, true);
    }

    public boolean isLoadingComplete() {
        return loadingComplete;
    }
}