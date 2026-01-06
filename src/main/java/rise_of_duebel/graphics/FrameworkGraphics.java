package rise_of_duebel.graphics;

import KAGO_framework.model.abitur.datenstrukturen.Stack;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class FrameworkGraphics {

    private Graphics2D graphics2D;
    private Stack<GraphicsState> stack = new Stack<>();

    public FrameworkGraphics(Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
    }

    public void push() {
        if (this.graphics2D == null) return;

        GraphicsState currentState = new GraphicsState(this.graphics2D);
        this.stack.push(currentState);
    }

    public void pop() {
        if (this.graphics2D == null) return;

        if (!this.stack.isEmpty()) {
            this.stack.top().apply(this.graphics2D);
            this.stack.pop();
        }
    }

    public Graphics2D getGraphics2D() {
        return this.graphics2D;
    }

    private class GraphicsState {
        private AffineTransform transform;
        private Color color;
        private Font font;
        private Stroke stroke;
        private Composite composite;
        private RenderingHints renderingHints;

        public GraphicsState(Graphics2D g) {
            this.transform = g.getTransform();
            this.color = g.getColor();
            this.font = g.getFont();
            this.stroke = g.getStroke();
            this.composite = g.getComposite();
            this.renderingHints = g.getRenderingHints();
        }

        public void apply(Graphics2D g) {
            g.setTransform(transform);
            g.setColor(color);
            g.setFont(font);
            g.setStroke(stroke);
            g.setComposite(composite);
            g.setRenderingHints(renderingHints);
        }

        public AffineTransform getTransform() {
            return transform;
        }

        public Color getColor() {
            return color;
        }

        public Font getFont() {
            return font;
        }

        public Stroke getStroke() {
            return stroke;
        }

        public Composite getComposite() {
            return composite;
        }

        public RenderingHints getRenderingHints() {
            return renderingHints;
        }
    }
}
