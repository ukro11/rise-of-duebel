package rise_of_duebel.graphics.level.impl;

import KAGO_framework.view.DrawTool;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.world.PhysicsWorld;
import rise_of_duebel.Wrapper;
import rise_of_duebel.animation.Easings;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.dyn4j.WorldCollider;
import rise_of_duebel.graphics.level.LevelColors;
import rise_of_duebel.graphics.level.LevelLoader;
import rise_of_duebel.graphics.level.LevelMap;
import rise_of_duebel.model.debug.VisualConstants;
import rise_of_duebel.model.user.UserProfile;
import rise_of_duebel.utils.ColorUtil;
import rise_of_duebel.utils.MathUtils;

import java.awt.*;

public class LevelStats extends LevelLoader {

    private Color TEXT_COLOR = Color.decode("#b29f99");
    private Font font;

    private double maxDistance = 100;

    private WorldCollider textCollider;
    private UserProfile localUserProfile;

    public LevelStats(LevelMap map) {
        super("stats.json", new LevelColors("#f4b13b", "#feab32", "#be7708", "#be7708", "#6603fc"), map);
        this.textCollider = this.map.getColliderByLayer("TEXT");
        this.font = VisualConstants.getFont(20);
    }

    @Override
    public void onActive() {
        this.localUserProfile = Wrapper.getLocalPlayer().getUserProfile();
        this.getUserProfiles().forEach(UserProfile::pause);
    }

    @Override
    public void loadCollider(WorldCollider wc, BodyFixture fix) {
        if (wc.getLayer().equals("TEXT")) {
            fix.setSensor(true);
        }
    }

    @Override
    public void updateCollider(TimeStep step, PhysicsWorld<ColliderBody, ?> world) {}

    @Override
    public void draw(DrawTool drawTool) {
        double dx = Wrapper.getLocalPlayer().getX() - (this.textCollider.getX() + this.textCollider.getWidth() / 2);
        double t = Easings.easeOutElastic(MathUtils.clamp(Math.abs(dx) / this.maxDistance, 0.0, 1.0));
        double dir = Math.signum(dx);
        Color target = (dx < 0) ? Color.RED : Color.GREEN;
        double maxOffsetPx = 100.0;
        double offsetX = dir * maxOffsetPx * t;

        drawTool.push();

        drawTool.getGraphics2D().setFont(this.font);

        drawTool.setCurrentColor(ColorUtil.lerp(this.TEXT_COLOR, target, t));
        String text = dir < 0 ? "NOCHMAL  SPIELEN" : "NÄCHSTES  LEVEL";
        drawTool.drawText(
            text,
            this.textCollider.getX() + (this.textCollider.getWidth() - drawTool.getFontWidth(text)) / 2 + offsetX,
            this.textCollider.getY() + drawTool.getFontHeight() + 30
        );
        drawTool.setCurrentColor(ColorUtil.lerp(this.TEXT_COLOR, target, t));
        String text2 = "DEATHS:" + this.localUserProfile.getDeaths();
        drawTool.drawText(
                text2,
                this.textCollider.getX() + (this.textCollider.getWidth() - drawTool.getFontWidth(text2)) / 2 + offsetX * 0.8,
                this.textCollider.getY() + drawTool.getFontHeight() + 60
        );
        drawTool.setCurrentColor(ColorUtil.lerp(this.TEXT_COLOR, target, t));
        String text3 = "TIME:" + formatSecondsToMMSS((int)this.localUserProfile.getTime());
        drawTool.drawText(
                text3,
                this.textCollider.getX() + (this.textCollider.getWidth() - drawTool.getFontWidth(text3)) / 2 + offsetX * 0.6,
                this.textCollider.getY() + drawTool.getFontHeight() + 90
        );


        drawTool.pop();
    }

    @Override
    public void enterPortal() {

    }

    @Override
    public void resetLevel() {
        Wrapper.getLevelManager().previousLevel(String.format("STATS-%d", Wrapper.getLevelManager().getIndex()));
    }
    private String formatSecondsToMMSS(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
