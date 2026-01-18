package rise_of_duebel.model.debug.impl;

import KAGO_framework.view.DrawTool;
import rise_of_duebel.Config;
import rise_of_duebel.Wrapper;
import rise_of_duebel.model.debug.VisualConstants;
import rise_of_duebel.model.debug.VisualModel;

import java.awt.*;

public class InfoComponent extends VisualModel {

    private final Font debugFont;
    private final double margin;
    private final double startY;

    public InfoComponent() {
        super("fps-component");
        double size = 24;
        this.debugFont = VisualConstants.getFont(VisualConstants.Fonts.DEBUG_FONT, size);
        this.margin = size + 5;
        this.startY = 60;
    }

   @Override
    public void draw(DrawTool drawTool) {
        drawTool.push();
        drawTool.setCurrentColor(new Color(47, 29, 3));
        drawTool.getGraphics2D().setFont(this.debugFont);

        drawTool.drawTextOutline(
                String.format("FPS: %s", Wrapper.getTimer().getFPS()),
                20,
                this.startY + this.margin,
                Color.decode("#b29f99"),
                5.0,
                Color.decode("#554544")
        );
        if (Wrapper.getLocalPlayer() != null && Config.RUN_ENV == Config.Environment.DEVELOPMENT) {
            drawTool.drawTextOutline(
                    String.format("X: %.2f", Wrapper.getLocalPlayer().getBody().getX()),
                    20,
                    this.startY + this.margin * 2,
                    Color.decode("#b29f99"),
                    5.0,
                    Color.decode("#554544")
            );
            drawTool.drawTextOutline(
                    String.format("Y: %.2f", Wrapper.getLocalPlayer().getBody().getY()),
                    20,
                    this.startY + this.margin * 3,
                    Color.decode("#b29f99"),
                    5.0,
                    Color.decode("#554544")
            );
        }
        drawTool.resetColor();
        drawTool.pop();
    }

    @Override
    public void update(double dt) {}
}
