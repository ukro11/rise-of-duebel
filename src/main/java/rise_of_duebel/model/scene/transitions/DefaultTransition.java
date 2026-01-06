package rise_of_duebel.model.scene.transitions;

import KAGO_framework.view.DrawTool;
import rise_of_duebel.Wrapper;
import rise_of_duebel.animation.Easings;
import rise_of_duebel.animation.tween.Tween;
import rise_of_duebel.model.scene.Scene;
import rise_of_duebel.model.scene.SceneTransition;

import java.awt.*;

public class DefaultTransition implements SceneTransition {

    private Color textColor;
    private Color transitionColor;

    private boolean outFinished;

    private Tween TWEEN_UPPER_RECTANGLE;
    private double UPPER_RECTANGLE_HEIGHT;

    private Tween TWEEN_LOWER_RECTANGLE;
    private double LOWER_RECTANGLE_Y;

    public DefaultTransition() {
        this.textColor = Color.decode("#d34f2b");
        this.transitionColor = Color.decode("#6c1103");

        this.outFinished = false;

        this.UPPER_RECTANGLE_HEIGHT = 0.0;
        this.TWEEN_UPPER_RECTANGLE = Tween.to(this.UPPER_RECTANGLE_HEIGHT, (double) (Wrapper.getScreenHeight() / 2 + 10), 0.35)
                .ease((x) -> Easings.easeInOutElastic(x))
                .loop(false);

        this.LOWER_RECTANGLE_Y = Wrapper.getScreenHeight();
        this.TWEEN_LOWER_RECTANGLE = Tween.to(this.LOWER_RECTANGLE_Y, (double) (Wrapper.getScreenHeight() / 2 - 10), 0.35)
                .ease((x) -> Easings.easeInOutElastic(x))
                .loop(false);
    }

    @Override
    public void in(Scene before) {
        this.TWEEN_UPPER_RECTANGLE.animate();
        this.TWEEN_LOWER_RECTANGLE.animate();
    }

    @Override
    public boolean swap() {
        return this.TWEEN_UPPER_RECTANGLE.isFinished() && this.TWEEN_LOWER_RECTANGLE.isFinished();
    }

    @Override
    public boolean finished() {
        return this.TWEEN_UPPER_RECTANGLE.isFinished() && this.TWEEN_LOWER_RECTANGLE.isFinished() && this.outFinished;
    }

    @Override
    public void out(Scene before, Scene after) {
        this.TWEEN_UPPER_RECTANGLE.redo(this.TWEEN_UPPER_RECTANGLE.getValueDouble(), 0.0, 0.5).delay(0.5).ease((x) -> Easings.easeInElastic((double) x)).animate();
        this.TWEEN_LOWER_RECTANGLE.redo(this.TWEEN_LOWER_RECTANGLE.getValueDouble(), (double) Wrapper.getScreenHeight(), 0.5).delay(0.5).ease((x) -> Easings.easeInElastic((double) x)).animate();
        this.TWEEN_LOWER_RECTANGLE.onFinish(t -> this.outFinished = true);
    }

    @Override
    public void draw(DrawTool drawTool) {
        drawTool.push();

        drawTool.setCurrentColor(this.transitionColor);

        double screenW = Wrapper.getScreenWidth();
        double screenH = Wrapper.getScreenHeight();

        double upperH = this.TWEEN_UPPER_RECTANGLE.getValueDouble();
        double lowerY = this.TWEEN_LOWER_RECTANGLE.getValueDouble();

        drawTool.drawFilledRectangle(0.0, 0.0, screenW, upperH);
        drawTool.drawFilledRectangle(0.0, lowerY, screenW, screenH);

        drawTool.pop();
    }
}
