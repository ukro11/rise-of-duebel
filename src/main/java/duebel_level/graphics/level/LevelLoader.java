package duebel_level.graphics.level;

import KAGO_framework.view.DrawTool;
import duebel_level.Wrapper;
import duebel_level.dyn4j.ColliderBody;
import duebel_level.dyn4j.WorldCollider;
import duebel_level.graphics.level.impl.LevelStats;
import duebel_level.model.scene.impl.GameScene;
import duebel_level.model.user.UserProfile;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/***
 * @author Mark
 */
public abstract class LevelLoader {

    protected final LevelMap map;
    protected final LevelColors colors;
    protected final World<ColliderBody> world;
    private static List<Class<LevelLoader>> loaders = new ArrayList<>();


    /**
     * Sammelt alle LevelLoader-Implementierungen aus dem impl-Package per Reflection.
     * LevelStats wird dabei ignoriert.
     */
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

    /**
     * Erstellt einen LevelLoader und setzt Hintergrund/World.
     * Prüft, ob der übergebene Dateiname zur Map passt.
     *
     * @param filename erwarteter Map-Dateiname
     * @param colors Level-Farben
     * @param map Level-Map
     */
    public LevelLoader(String filename, LevelColors colors, LevelMap map) {
        if (!map.getFileName().equals(filename)) throw new RuntimeException(String.format("The assigned level code does not match with the map:\nmap: %s\ncode:%s", map.getFileName(), filename));
        this.colors = colors;
        this.map = map;
        this.world = Wrapper.getEntityManager().getWorld();
        GameScene.getInstance().setBackground(colors.background());
    }

    /**
     * Aktualisiert Collider/Physics-Logik des Levels.
     *
     * @param step Zeitschritt
     * @param world Physics-World
     */
    public abstract void updateCollider(TimeStep step, PhysicsWorld<ColliderBody, ?> world);

    /** Wird aufgerufen, wenn ein Portal betreten/ausgelöst wird. */
    public abstract void enterPortal();

    /** Setzt das Level auf den Startzustand zurück. */
    public abstract void resetLevel();

    /**
     * Callback, wenn der Loader aktiv wird.
     * Startet UserProfiles (außer bei LevelStats).
     */
    public void onActive() {
        if (!(this instanceof LevelStats)) {
            this.getUserProfiles().forEach(UserProfile::start);
        }
    }

    /**
     * Callback pro Collider beim Laden.
     *
     * @param wc Collider-Wrapper
     * @param fix Fixture
     */
    public void loadCollider(WorldCollider wc, BodyFixture fix) {}

    /**
     * Zeichnen pro Frame für level-spezifische Inhalte.
     *
     * @param drawTool DrawTool
     */
    public void draw(DrawTool drawTool) {}

    /**
     * Debug-Zeichnen eines Colliders anhand seiner Shape und Layer-Farbe.
     *
     * @param wc Collider-Wrapper
     * @param drawTool DrawTool
     */
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

    /**
     * Findet eine Loader-Klasse anhand der Index-Nummer im Klassennamen (z.B. Level3 -> 3).
     *
     * @param index Level-Index
     * @return passende Loader-Klasse oder null
     */
    public static Class<LevelLoader> getLoaderByIndex(int index) {
        return LevelLoader.loaders.stream().filter(l -> {
            String str = l.getSimpleName().replace("Level", "");
            if (str == null || str.isEmpty() || str.isBlank()) return false;
            return Integer.parseInt(str) == index;
        }).findFirst().orElse(null);
    }

    /**
     * @return Level-Farben
     */
    public LevelColors getColors() {
        return this.colors;
    }

    /**
     * @return alle UserProfiles der aktuellen PlayerEntities
     */
    public List<UserProfile> getUserProfiles() {
        return Wrapper.getEntityManager().getPlayerEntities().stream().map(e -> e.getUserProfile()).toList();
    }
}
