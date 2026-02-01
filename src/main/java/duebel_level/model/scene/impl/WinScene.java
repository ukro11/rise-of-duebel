package duebel_level.model.scene.impl;

import KAGO_framework.model.abitur.datenstrukturen.Queue;
import KAGO_framework.view.DrawTool;
import duebel_level.Config;
import duebel_level.Wrapper;
import duebel_level.model.debug.VisualConstants;
import duebel_level.model.scene.Scene;
import duebel_level.model.user.ProfileStats;

import java.awt.*;
import java.awt.event.KeyEvent;

public class WinScene extends Scene {

    private Font titleFont;
    private Font statsFont;
    private Queue<ProfileStats> statsQueue;

    public WinScene() {
        super("win");
        this.titleFont = VisualConstants.getFont(100);
        this.statsFont = VisualConstants.getFont(50);
        this.statsQueue = Wrapper.getLocalPlayer().getUserProfile().getPastStats();
    }

    @Override
    public void update(double dt) {}

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.push();
        drawTool.setCurrentColor(new Color(238, 195, 154));
        drawTool.drawFilledRectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        drawTool.setCurrentColor(VisualConstants.TEXT_COLOR);
        drawTool.getGraphics2D().setFont(this.titleFont);
        drawTool.drawCenteredText("GEWONNEN!!!", 0, -100, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        drawTool.setCurrentColor(VisualConstants.TEXT_COLOR);
        drawTool.getGraphics2D().setFont(this.statsFont);

        ProfileStats past = this.statsQueue.front();
        if (past != null) {
            double startY = (drawTool.getFontHeight() + 80);
            drawTool.drawCenteredText("LEVEL: " + past.getLevel(), 0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            drawTool.setCurrentColor(Color.GREEN);
            drawTool.drawCenteredText("TIME: " + this.formatSecondsToMMSS((int)past.getTime()), 0, startY, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            drawTool.setCurrentColor(Color.RED);
            drawTool.drawCenteredText("DEATHS: " + past.getDeaths(), 0, startY * 2, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

            drawTool.setCurrentColor(VisualConstants.TEXT_COLOR);
            drawTool.drawCenteredText("PRESS ENTER", 0, startY * 3, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        }
        drawTool.pop();
    }

    private String formatSecondsToMMSS(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.statsQueue.dequeue();
            if (this.statsQueue.isEmpty()) {
                Wrapper.getViewController().shutdown();
            }
        }
    }
}