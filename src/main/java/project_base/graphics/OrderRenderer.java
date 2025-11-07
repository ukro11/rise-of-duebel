package project_base.graphics;

import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderRenderer {

    private final Logger logger = LoggerFactory.getLogger(OrderRenderer.class);
    private final ArrayList<IOrderRenderer> drawables;

    public OrderRenderer() {
        this.drawables = new ArrayList<>();
    }

    public void draw(DrawTool drawTool) {
        this.drawables.sort(Comparator.comparing(IOrderRenderer::zIndex));
        for (int i = 0; i < this.drawables.size(); i++) {
            var d = this.drawables.get(i);
            if (d.shouldRender()) {
                d.draw(drawTool);
            }
        }
    }

    public void register(IOrderRenderer renderer) {
        if (renderer == null) {
            this.logger.warn("Registered renderer is null");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (!this.drawables.contains(renderer)) {
                this.drawables.add(renderer);
            }
        });
    }

    public void unregister(IOrderRenderer renderer) {
        SwingUtilities.invokeLater(() -> {
            this.drawables.remove(renderer);
        });
    }

    public void registerAll(List<IOrderRenderer> integration) {
        SwingUtilities.invokeLater(() -> {
            this.drawables.addAll(integration);
        });
    }

    public List<IOrderRenderer> getDrawables() {
        return this.drawables;
    }
}
