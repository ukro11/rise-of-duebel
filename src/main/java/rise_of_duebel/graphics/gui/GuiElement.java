package rise_of_duebel.graphics.gui;

import KAGO_framework.view.DrawTool;
import org.dyn4j.geometry.Vector2;
import rise_of_duebel.utils.MathUtils;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/***
 * @author Mark
 */
public abstract class GuiElement<T> {

    protected Gui gui;
    private double x;
    private double y;
    protected double width;
    protected double height;
    protected Vector2 offset;
    protected GuiElementPosition horizontal;
    protected GuiElementPosition vertical;
    protected T value;

    public GuiElement(GuiSizing sizing) {
        this.x = sizing.getX();
        this.y = sizing.getY();
        this.width = sizing.getWidth();
        this.height = sizing.getHeight();
        this.horizontal = sizing.getHorizontal();
        this.vertical = sizing.getVertical();
    }

    public void init(Gui gui) {
        this.gui = gui;
        switch (this.horizontal) {
            case START -> this.x = this.gui.x;
            case CENTER -> this.x = this.gui.x + (this.gui.width - this.width) / 2;
            case END -> this.x = this.gui.x + this.gui.width - this.width;
            case CUSTOM -> this.x += this.gui.x;
        }
        switch (this.vertical) {
            case START -> this.y = this.gui.y;
            case CENTER -> this.y = this.gui.y + (this.gui.height - this.height) / 2;
            case END -> this.y = this.gui.y + this.gui.height - this.height;
            case CUSTOM -> this.y += this.gui.y;
        }
    }

    public abstract void update(double dt);
    public abstract void draw(DrawTool drawTool);

    /***
     * Überprüft, ob der Mauszeiger über dem Element ist
     * @param mouseX Maus x-Koordinate
     * @param mouseY Mause y-Koordinate
     * @return true, wenn der Mauszeiger über dem Element ist
     */
    public boolean isHover(int mouseX, int mouseY) {
        return MathUtils.isHover(this.getX(), this.getY(), this.width, this.height, mouseX, mouseY);
    }

    public void setOffset(Vector2 off) {
        this.offset = off;
    }

    public double getX() {
        return this.x + this.offset.x;
    }

    public double getY() {
        return this.y + this.offset.y;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
