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
import rise_of_duebel.graphics.level.impl.LevelStats;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.scene.impl.GameScene;
import rise_of_duebel.model.user.UserProfile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class LevelLoader {

    protected final LevelMap map;
    protected final LevelColors colors;
    protected final World<ColliderBody> world;
    protected final List<UserProfile> userProfiles;
    private static List<Class<LevelLoader>> loaders = new ArrayList<>();

    static {
        try {
            String packageName = String.format("%s.impl", LevelLoader.class.getPackageName());
            String packagePath = packageName.replace('.', '/');

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.toURI());

                if (!directory.exists()) continue;

                File[] files = directory.listFiles((dir, name) -> name.endsWith(".class") && !name.equals("LevelStats.class"));
                if (files == null) continue;

                for (File file : files) {
                    String className = file.getName()
                            .substring(0, file.getName().length() - 6);

                    String fullName = packageName + "." + className;
                    LevelLoader.loaders.add((Class<LevelLoader>) Class.forName(fullName));
                }
            }

        } catch (IOException | ClassNotFoundException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public LevelLoader(String filename, LevelColors colors, LevelMap map, List<UserProfile> userProfiles) {
        if (!map.getFileName().equals(filename)) throw new RuntimeException(String.format("The assigned level code does not match with the map:\nmap: %s\ncode:%s", map.getFileName(), filename));
        this.userProfiles = userProfiles;
        this.colors = colors;
        this.map = map;
        this.world = Wrapper.getEntityManager().getWorld();
        GameScene.getInstance().setBackground(colors.background());
    }

    public abstract void updateCollider(TimeStep step, PhysicsWorld<ColliderBody, ?> world);
    public abstract void enterPortal();
    public abstract void resetLevel();

    public void onActive() {
        if (!(this instanceof LevelStats)) {
            userProfiles.forEach(UserProfile::start);
            // userProfiles.forEach(u -> u.start());
        }
    }
    public void loadCollider(WorldCollider wc, BodyFixture fix) {}
    public void draw(DrawTool drawTool) {}

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

    public static Class<LevelLoader> getLoaderByIndex(int index) {
        return LevelLoader.loaders.stream().filter(l -> {
            String str = l.getSimpleName().replace("Level", "");
            if (str == null || str.isEmpty() || str.isBlank()) return false;
            return Integer.parseInt(str) == index;
        }).findFirst().orElse(null);
    }

    public List<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public UserProfile getUserProfile(EntityPlayer e) {
        return this.userProfiles.stream().filter(p -> p.getPlayer().equals(e)).findFirst().orElse(null);
    }
}
