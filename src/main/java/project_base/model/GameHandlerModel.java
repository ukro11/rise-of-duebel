package project_base.model;

import KAGO_framework.view.DrawTool;
import project_base.Config;
import project_base.model.scene.LoseScene;
import project_base.model.scene.Scene;
import project_base.model.scene.WinScene;
import project_base.model.debug.VisualConstants;

import java.awt.*;

public class GameHandlerModel {

    private double timer;
    private double timerDuration = 60 * 5;
    private GameState state;
    private int score;

    private final Font CARD_FONT;
    private final Font SCORE_FONT;
    private Color CARD_TEXT_COLOR = Color.decode("#b29f99");
    private Color CARD_OUTLINE_COLOR = Color.decode("#554544");

    public GameHandlerModel() {
        this.timer = this.timerDuration;
        this.score = 0;

        this.CARD_FONT = VisualConstants.getFont(30);
        this.SCORE_FONT = VisualConstants.getFont(25);
    }

    public void update(double dt) {
        if (this.timer > 0) {
            if (this.state == null) {
                this.state = GameState.RUNNING;
            }
            this.timer -= dt;

            if (this.timer < 0) this.timer = 0;

        } else if (this.timer == 0) {
            this.state = GameState.END;
            if (this.score >= 250) {
                Scene.open(new WinScene());

            } else {
                Scene.open(new LoseScene());
            }
        }
    }

    public void draw(DrawTool drawTool) {
        this.drawTimer(drawTool);
        this.drawPoints(drawTool);
    }

    private void drawPoints(DrawTool drawTool) {
        drawTool.push();

        int margin = 5;
        int width = 200;
        int height = 50;

        drawTool.setCurrentColor(Color.decode("#6c6e85"));
        drawTool.drawFilledRectangle(Config.WINDOW_WIDTH - width - margin, margin, width, height);

        drawTool.setCurrentColor(Color.decode("#3a3a50"));
        drawTool.getGraphics2D().setStroke(new BasicStroke(3.0f));
        drawTool.drawRectangle(Config.WINDOW_WIDTH - width - margin, margin, width, height);

        drawTool.getGraphics2D().setFont(this.SCORE_FONT);
        drawTool.drawCenteredTextOutline(
                String.format("Punkte: %d", this.score).toUpperCase(),
                Config.WINDOW_WIDTH - width - margin,
                margin - 1,
                width,
                height,
                this.CARD_TEXT_COLOR,
                5.0,
                this.CARD_OUTLINE_COLOR
        );
        drawTool.pop();
    }

    private void drawTimer(DrawTool drawTool) {
        drawTool.push();

        int margin = 5;
        int width = 200;
        int height = 50;

        drawTool.setCurrentColor(Color.decode("#6c6e85"));
        drawTool.drawFilledRectangle(Config.WINDOW_WIDTH - width - margin, (Config.WINDOW_HEIGHT - 29) - height - margin, width, height);

        drawTool.setCurrentColor(Color.decode("#3a3a50"));
        drawTool.getGraphics2D().setStroke(new BasicStroke(3.0f));
        drawTool.drawRectangle(Config.WINDOW_WIDTH - width - margin, (Config.WINDOW_HEIGHT - 29) - height - margin, width, height);

        drawTool.getGraphics2D().setFont(this.CARD_FONT);
        drawTool.drawCenteredTextOutline(
                this.formatSecondsToMMSS((int) this.timer),
                Config.WINDOW_WIDTH - width - margin,
                (Config.WINDOW_HEIGHT - 29) - height - margin,
                width,
                height,
                this.CARD_TEXT_COLOR,
                5.0,
                this.CARD_OUTLINE_COLOR
        );
        drawTool.pop();
    }

    private String formatSecondsToMMSS(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public int getScore() {
        return this.score;
    }

    public enum GameState {
        RUNNING,
        END,
    }
}
