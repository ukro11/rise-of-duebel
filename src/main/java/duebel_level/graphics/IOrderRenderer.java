package duebel_level.graphics;

import KAGO_framework.view.DrawTool;

public interface IOrderRenderer {
    double zIndex();
    void draw(DrawTool drawTool);
    default boolean shouldRender() {
        return true;
    }
}
