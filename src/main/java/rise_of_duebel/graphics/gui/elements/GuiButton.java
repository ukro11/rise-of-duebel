package rise_of_duebel.graphics.gui.elements;

import KAGO_framework.view.DrawTool;
import rise_of_duebel.Wrapper;
import rise_of_duebel.graphics.gui.GuiElement;
import rise_of_duebel.graphics.gui.GuiSizing;
import rise_of_duebel.model.debug.VisualConstants;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/***
 * @author Mark
 */
public class GuiButton extends GuiElement {

    private final String text;

    private final Color textColor;
    private final Font textFont;

    private final Color backgroundColor;
    private final Color shadowColor;

    private Consumer<GuiButton> click;

    public GuiButton(String text, GuiSizing sizing, int fontSize) {
        super(sizing);
        this.text = text;

        this.textColor = Color.decode("#fcf965");
        this.textFont = VisualConstants.getFont(fontSize);

        this.backgroundColor = Color.decode("#6c1103");
        this.shadowColor = Color.decode("#2c0601");
    }

    public GuiButton onClick(Consumer<GuiButton> click) {
        this.click = click;
        return this;
    }

    @Override
    public void update(double dt) {}

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.push();

        drawTool.setCurrentColor(this.shadowColor);
        drawTool.drawFilledRectangle(this.getX(), this.getY(), this.width, this.height + 5);

        drawTool.setCurrentColor(this.isHover(Wrapper.getViewController().getMouseX(), Wrapper.getViewController().getMouseY()) ? this.backgroundColor.brighter() : this.backgroundColor);
        drawTool.drawFilledRectangle(this.getX(), this.getY(), this.width, this.height);

        drawTool.getGraphics2D().setFont(this.textFont);
        drawTool.setCurrentColor(this.isHover(Wrapper.getViewController().getMouseX(), Wrapper.getViewController().getMouseY()) ? this.textColor.brighter() : this.textColor);
        drawTool.drawText(this.text, this.getX() + (this.width - drawTool.getFontWidth(this.text)) / 2, this.getY() + (this.height) / 2 + drawTool.getFontHeight());

        drawTool.pop();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.isHover(e.getX(), e.getY())) {
            if (this.click != null) this.click.accept(this);
        }
    }
}
