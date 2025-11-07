package project_base.graphics.tooltip;

import KAGO_framework.view.DrawTool;
import project_base.Config;
import project_base.Wrapper;
import project_base.animation.Easings;
import project_base.animation.tween.Tween;
import project_base.model.KeyManagerModel;
import project_base.model.debug.VisualConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class Tooltip {

    private static final Logger log = LoggerFactory.getLogger(Tooltip.class);
    private final KeyManagerModel keyModel;
    private final Function<KeyManagerModel, String> function;
    private final double iconRatio;
    private final int iconWidth;
    private final int iconHeight;

    private String textCache;
    private double lastX;

    private final Font TOOLTIP_FONT;
    private double TOOLTIP_TWEEN_DURATION = 1.6;
    private double TOOLTIP_TWEEN_POSITION_START = Config.WINDOW_HEIGHT + 20 - Wrapper.getTooltipManager().getStartY();
    private Color TOOLTIP_TEXT_COLOR = Color.decode("#b29f99");
    private Color TOOLTIP_OUTLINE_COLOR = Color.decode("#554544");
    private Tween TOOLTIP_TWEEN_POSITION_X;
    private Tween TOOLTIP_TWEEN_POSITION_Y;

    public Tooltip(KeyManagerModel keyModel, Function<KeyManagerModel, String> function) {
        this.keyModel = keyModel;
        this.function = function;
        this.textCache = function.apply(this.keyModel) != null ? function.apply(this.keyModel).toUpperCase().replace(" ", "  ") : "";

        this.iconHeight = 30;
        this.iconRatio = this.iconHeight / this.getKeyModel().getIcon().getHeight();
        this.iconWidth = (int) (this.getKeyModel().getIcon().getWidth() * this.iconRatio);

        this.TOOLTIP_FONT = VisualConstants.getFont(20);
        this.TOOLTIP_TWEEN_POSITION_X =
                Tween.to(20.0, 20.0, this.TOOLTIP_TWEEN_DURATION / 4)
                        .ease((x) -> Easings.easeOutCubic(x))
                        .delay(Wrapper.getTooltipManager().getTooltips().size() * 0.3)
                        .loop(false);
        this.TOOLTIP_TWEEN_POSITION_Y =
                Tween.to(this.TOOLTIP_TWEEN_POSITION_START, 0.0, this.TOOLTIP_TWEEN_DURATION)
                        .ease((x) -> Easings.easeOutElastic(x))
                        .delay(Wrapper.getTooltipManager().getTooltips().size() * 0.3 + this.TOOLTIP_TWEEN_DURATION)
                        .loop(false);

        if (this.showTooltip()) this.TOOLTIP_TWEEN_POSITION_Y.animate();
    }

    public KeyManagerModel getKeyModel() {
        return this.keyModel;
    }

    public String getDescription() {
        return this.textCache.toUpperCase().replace(" ", "  ");
    }

    public boolean showTooltip() {
        return this.function.apply(this.keyModel) != null;
    }

    public String getCurrentDescription() {
        return this.function.apply(this.keyModel);
    }

    public double getTooltipWidth(DrawTool drawTool) {
        if (this.getDescription() == null) return 0;
        return drawTool.getFontWidth(drawTool.getGraphics2D().getFont(), this.getDescription()) + this.keyModel.getIcon().getWidth();
    }

    public void update(double dt) {
        try {
            if (this.getCurrentDescription() != null && !this.getCurrentDescription().equals(this.textCache)) {
                this.textCache = this.getCurrentDescription().toUpperCase().replace(" ", "  ");
            }

        } catch (NullPointerException e) {
            log.error("Ignore error, java bug", e);
        }

        if (this.showTooltip() && (double) this.TOOLTIP_TWEEN_POSITION_Y.getTweenValue().getTarget() == this.TOOLTIP_TWEEN_POSITION_START) {
            this.TOOLTIP_TWEEN_POSITION_Y.redo(this.TOOLTIP_TWEEN_POSITION_Y.getValueDouble(), 0.0, this.TOOLTIP_TWEEN_DURATION);
            this.TOOLTIP_TWEEN_POSITION_Y.animate();

        } else if (!this.showTooltip() && (double) this.TOOLTIP_TWEEN_POSITION_Y.getTweenValue().getTarget() == 0.0) {
            this.TOOLTIP_TWEEN_POSITION_Y.redo(this.TOOLTIP_TWEEN_POSITION_Y.getValueDouble(), this.TOOLTIP_TWEEN_POSITION_START, this.TOOLTIP_TWEEN_DURATION);
            this.TOOLTIP_TWEEN_POSITION_Y.animate();

        } else if (this.showTooltip() && (double) this.TOOLTIP_TWEEN_POSITION_Y.getTweenValue().getTarget() == 0.0 && !this.TOOLTIP_TWEEN_POSITION_Y.isRunning() && !this.TOOLTIP_TWEEN_POSITION_Y.isFinished()) {
            this.TOOLTIP_TWEEN_POSITION_Y.animate();

        }
    }

    public void draw(DrawTool drawTool, double x, double y) {
        BufferedImage icon = this.keyModel.getIcon();

        if (this.lastX != x) {
            this.TOOLTIP_TWEEN_POSITION_X.redo(this.TOOLTIP_TWEEN_POSITION_X.getValueDouble(), x, this.TOOLTIP_TWEEN_DURATION / 4);
            this.TOOLTIP_TWEEN_POSITION_X.animate();
            this.lastX = x;
        }

        drawTool.getGraphics2D().setFont(this.TOOLTIP_FONT);
        if (this.textCache != null && !this.textCache.equals("")) {
            drawTool.drawTextOutline(
                this.textCache,
                this.TOOLTIP_TWEEN_POSITION_X.getValueDouble() + this.iconWidth + 7.5,
                y + this.TOOLTIP_TWEEN_POSITION_Y.getValueDouble() + 22.5,
                this.TOOLTIP_TEXT_COLOR,
                5.0,
                this.TOOLTIP_OUTLINE_COLOR
            );
        }

        drawTool.getGraphics2D().drawImage(icon, (int) this.TOOLTIP_TWEEN_POSITION_X.getValueDouble(), (int) (y + this.TOOLTIP_TWEEN_POSITION_Y.getValueDouble()), this.iconWidth, this.iconHeight, null);
    }
}
