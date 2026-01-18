package rise_of_duebel.graphics.gui;

import KAGO_framework.view.DrawTool;
import rise_of_duebel.Wrapper;
import rise_of_duebel.graphics.level.LevelColors;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/***
 * @author Mark
 */
public abstract class Gui {

    public static List<Gui> guis = new ArrayList<>();
    private static double GUI_SIZE_PERCENTAGE = 0.7;

    protected Gui parent;

    protected double x;
    protected double y;
    protected final double width;
    protected final double height;

    private Color background;

    protected final List<GuiElement> elements;

    public Gui() {
        this(Wrapper.getScreenWidth() * Gui.GUI_SIZE_PERCENTAGE, Wrapper.getScreenHeight() * Gui.GUI_SIZE_PERCENTAGE);
    }

    public Gui(double width, double height) {
        this.elements = new ArrayList<>();
        this.x = (Wrapper.getScreenWidth() - width) / 2;
        this.y = (Wrapper.getScreenHeight() - height) / 2;
        this.width = width;
        this.height = height;
        this.background = LevelColors.createDefault().accent();
    }

    public static void registerGui(Gui gui) {
        Gui.guis.add(gui);
    }

    public void addElement(GuiElement element) {
        this.elements.add(element);
    }

    public void removeElement(GuiElement element) {
        this.elements.remove(element);
    }

    /***
     * Überprüft, ob Bedingungen erfüllt sind, um das GUI zu öffnen (neben dem Key)
     * @return true/false, ob der Screen geöffnet werden darf
     */
    public boolean shouldOpen() {
        return true;
    }

    /***
     * Welche Taste benötigt wird, um das GUI zu öffnen
     * @return -1 heißt kein Key gesetzt und muss manuell geöffnet werden
     */
    public int keyToOpen() {
        return -1;
    }

    /***
     * Ob das Spiel, das während das GUI offenen ist, pausiert werden soll
     * @return true, wenn das Spiel es pausiert werden soll und false, wenn nicht
     */
    public boolean pauseGame() {
        return true;
    }

    public void update(double dt) {
        if (this.background == null || !this.background.equals(Wrapper.getLevelManager().getCurrent())) {
            this.background = Wrapper.getLevelManager().getCurrent().getLoader().getColors().accent();
        }

        this.elements.forEach(it -> it.update(dt));
    }

    public void draw(DrawTool drawTool) {
        drawTool.push();

        drawTool.setCurrentColor(this.background);
        drawTool.drawFilledRectangle(this.x, this.y, this.width, this.height);

        drawTool.setCurrentColor(this.background.darker());
        drawTool.setLineWidth(5);
        drawTool.drawRectangle(this.x, this.y, this.width, this.height);

        drawTool.resetColor();
        this.elements.forEach(it -> it.draw(drawTool));

        drawTool.pop();
    }

    public void onGuiOpen() {
        this.elements.forEach(it -> it.init(this));
    }

    public void onGuiClose() {}

    public void mouseEntered(MouseEvent e) {
        this.elements.forEach(it -> it.mouseEntered(e));
    }

    public void mouseExited(MouseEvent e) {
        this.elements.forEach(it -> it.mouseExited(e));
    }

    public void mouseReleased(MouseEvent e) {
        this.elements.forEach(it -> it.mouseReleased(e));
    }

    public void mouseClicked(MouseEvent e) {
        this.elements.forEach(it -> it.mouseClicked(e));
    }

    public void mouseDragged(MouseEvent e) {
        this.elements.forEach(it -> it.mouseDragged(e));
    }

    public void mouseMoved(MouseEvent e) {
        this.elements.forEach(it -> it.mouseMoved(e));
    }

    public void mousePressed(MouseEvent e) {
        this.elements.forEach(it -> it.mousePressed(e));
    }

    public void keyTyped(KeyEvent e) {
        this.elements.forEach(it -> it.keyTyped(e));
    }

    public void keyPressed(KeyEvent e) {
        this.elements.forEach(it -> it.keyPressed(e));
    }

    public void keyReleased(KeyEvent e) {
        this.elements.forEach(it -> it.keyReleased(e));
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public Gui getParent() {
        return this.parent;
    }
}
