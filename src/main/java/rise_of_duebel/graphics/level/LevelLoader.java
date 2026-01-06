package rise_of_duebel.graphics.level;

import KAGO_framework.view.DrawTool;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;
import rise_of_duebel.Wrapper;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.dyn4j.WorldCollider;
import rise_of_duebel.model.scene.impl.GameScene;

public abstract class LevelLoader {

    protected final LevelMap map;
    protected final LevelColors colors;
    protected final World<ColliderBody> world;

    public LevelLoader(String filename, LevelColors colors, LevelMap map) {
        if (!map.getFileName().equals(filename)) throw new RuntimeException(String.format("The assigned level code does not match with the map:\nmap: %s\ncode:%s", map.getFileName(), filename));
        this.colors = colors;
        this.map = map;
        this.world = Wrapper.getEntityManager().getWorld();
        GameScene.getInstance().setBackground(colors.background());
    }

    public abstract void updateCollider(TimeStep step, PhysicsWorld<ColliderBody, ?> world);
    public abstract void resetLevel();

    public void drawCollider(WorldCollider wc, DrawTool drawTool) {
        BodyFixture f = wc.getFixture();
        drawTool.push();
        drawTool.setCurrentColor(this.colors.getColorByLayer(wc.getLayer()));
        if (f.getShape() instanceof Polygon) {
            drawTool.drawFilledRectangle(wc.getX(), wc.getY(), wc.getWidth(), wc.getHeight());

        } else if (f.getShape() instanceof Ellipse) {
            drawTool.drawFilledEllipse(wc.getX(), wc.getY(), wc.getWidth(), wc.getHeight());
        }
        drawTool.pop();
    }
}
