package duebel_level.graphics.tooltip;

import KAGO_framework.view.DrawTool;
import duebel_level.Config;

import java.util.concurrent.CopyOnWriteArrayList;

public class TooltipManager {

    private final CopyOnWriteArrayList<Tooltip> tooltips = new CopyOnWriteArrayList<>();

    private double TOOLTIP_START_X = 30;
    private double TOOLTIP_START_Y = Config.WINDOW_HEIGHT - 90;
    private double TOOLTIP_MARGIN_X = 40;

    public void register(Tooltip tooltip) {
        this.tooltips.add(tooltip);
    }

    public void update(double dt) {
        this.tooltips.forEach(t -> t.update(dt));
    }

    public void draw(DrawTool drawTool) {
        double lastX = this.TOOLTIP_START_X;

        drawTool.push();
        for (int i = this.tooltips.size() - 1; i >= 0; i--) {
            var tooltip = this.tooltips.get(i);
            // ((this.tooltips.size() - 1) - i)
            tooltip.draw(drawTool, lastX, this.TOOLTIP_START_Y);
            if (tooltip.showTooltip()) {
                lastX += tooltip.getTooltipWidth(drawTool) + this.TOOLTIP_MARGIN_X;
            }
        }
        drawTool.pop();
    }

    public double getStartX() {
        return this.TOOLTIP_START_X;
    }

    public double getStartY() {
        return this.TOOLTIP_START_Y;
    }

    public CopyOnWriteArrayList<Tooltip> getTooltips() {
        return this.tooltips;
    }
}
