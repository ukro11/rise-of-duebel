package duebel_level.graphics.gui.elements;

import KAGO_framework.view.DrawTool;
import duebel_level.Wrapper;
import duebel_level.graphics.gui.GuiElement;
import duebel_level.graphics.gui.GuiSizing;
import duebel_level.model.debug.VisualConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

/***
 * @author Mark
 */
public class GuiToggle extends GuiElement<Boolean> {

    private static final Logger log = LoggerFactory.getLogger(GuiToggle.class);

    private final Supplier<String> text;

    private final Color textColor;
    private final Font textFont;

    private final Color backgroundColor;
    private final Color shadowColor;

    private Consumer<GuiToggle> click;
    private Consumer<Boolean> valueChange;

    public GuiToggle(String label, boolean val, GuiSizing sizing, int fontSize) {
        super(sizing);
        this.value = val;
        this.text = () -> String.format("%s:  %s", label, String.valueOf(this.value).toUpperCase());
        this.click = (btn) -> {
            this.value = !this.value;
            if (this.valueChange != null) this.valueChange.accept(this.value);
        };

        this.textColor = Color.decode("#fcf965");
        this.textFont = VisualConstants.getFont(fontSize);

        this.backgroundColor = Color.decode("#6c1103");
        this.shadowColor = Color.decode("#2c0601");
    }

    public GuiToggle onClick(Consumer<GuiToggle> click) {
        this.click = (btn) -> {
            this.value = !((boolean)this.value);
            if (this.valueChange != null) this.valueChange.accept((Boolean) value);
            if (click != null) click.accept(btn);
        };
        return this;
    }

    public GuiToggle onValueChange(Consumer<Boolean> change) {
        this.valueChange = change;
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
        drawTool.drawText(this.text.get(), this.getX() + (this.width - drawTool.getFontWidth(this.text.get())) / 2, this.getY() + (this.height) / 2 + drawTool.getFontHeight());

        drawTool.pop();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.isHover(e.getX(), e.getY())) {
            if (this.click != null) this.click.accept(this);
        }
    }
}
