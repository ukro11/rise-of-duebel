package duebel_level.graphics.gui;

public class GuiSizing {

    private double x;
    private double y;
    private double width;
    private double height;
    private GuiElementPosition horizontal;
    private GuiElementPosition vertical;

    public GuiSizing(GuiElementPosition position, double width, double height) {
        this.width = width;
        this.height = height;
        this.horizontal = position;
        this.vertical = position;
    }

    public GuiSizing(GuiElementPosition horizontal, GuiElementPosition vertical, double width, double height) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.width = width;
        this.height = height;
    }

    public GuiSizing(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static GuiSizing of(GuiElementPosition position, double width, double height) {
        return new GuiSizing(position, width, height);
    }

    public static GuiSizing of(GuiElementPosition horizontal, GuiElementPosition vertical, double width, double height) {
        return new GuiSizing(horizontal, vertical, width, height);
    }

    public static GuiSizing of(double x, double y, double width, double height) {
        return new GuiSizing(x, y, width, height);
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

    public GuiElementPosition getHorizontal() {
        return this.horizontal;
    }

    public GuiElementPosition getVertical() {
        return this.vertical;
    }
}
