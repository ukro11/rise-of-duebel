package KAGO_framework.control;

import KAGO_framework.Config;
import KAGO_framework.view.DrawFrame;
import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.ProgramController;
import rise_of_duebel.Wrapper;
import rise_of_duebel.event.events.KeyPressedEvent;
import rise_of_duebel.model.scene.GameScene;
import rise_of_duebel.model.scene.LoadingScene;
import rise_of_duebel.model.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Diese Klasse kontrolliert die DrawingPanels einer ihr zugewiesenen DrawingFrame.
 * Sie kann verschiedene Objekte erzeugen und den Panels hinzufuegen.
 * Vorgegebene Klasse des Frameworks. Modifikation auf eigene Gefahr.
 */
public class ViewController extends Canvas implements KeyListener, MouseListener, MouseMotionListener {

    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);
    private final ProgramController programController;
    private final DrawTool drawTool;

    private final AtomicBoolean watchPhysics;
    private final AtomicBoolean initializing;

    private DrawFrame drawFrame;

    // Instanzvariablen für gedrückte Tasten und Mausknöpfe
    private final static java.util.List<Integer> currentlyPressedKeys = new ArrayList<>();
    private final static java.util.List<Integer> currentlyPressedMouseButtons = new ArrayList<>();

    private static ViewController instance;

    /**
     * Erzeugt ein Objekt zur Kontrolle des Programmflusses.
     */
    public ViewController() {
        ViewController.instance = this;
        this.programController = new ProgramController(this);
        this.drawTool = new DrawTool();
        this.watchPhysics = new AtomicBoolean(true);
        this.initializing = new AtomicBoolean(true);

        this.programController.preStartProgram();
        boolean wm = false;
        while (this.initializing.get()) {
            if (!wm) {
                logger.info("Initializing...");
                wm = true;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("PreStart Setup is finished.");
        logger.info("Starting Engine.");

        this.createWindow();

        if (!rise_of_duebel.Config.SHOW_DEFAULT_WINDOW) {
            if(Config.INFO_MESSAGES) System.out.println("** Achtung! Standardfenster deaktiviert => wird nicht angezeigt.). **");
        }

        this.startProgram();
    }

    public static ViewController getInstance() {
        return instance;
    }

    /**
     * Startet das Programm, nachdem Vorarbeiten abgeschlossen sind.
     */
    private void startProgram() {
        try {
            this.setWatchPhyics(false);
            this.startGameEngine();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startGameEngine() throws InterruptedException {
        this.programController.startProgram();
        this.setWatchPhyics(true);
        Wrapper.getProcessManager().processPostGame();

        // TODO: https://github.com/dyn4j/dyn4j-samples/blob/master/src/main/java/org/dyn4j/samples/framework/SimulationFrame.java Line 305
        this.createBufferStrategy(3);

        this.requestFocus();

        var t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();

                    Wrapper.getTimer().update();
                    double dt = Wrapper.getTimer().getDeltaTime();

                    programController.updateProgram(dt);
                    Wrapper.getEntityManager().updateWorld(dt);
                    if (Scene.getCurrentScene() != null) Scene.getCurrentScene().update(dt);

                    drawTool.setGraphics2D(g);
                    if (Scene.getCurrentScene() != null) Scene.getCurrentScene().draw(drawTool);

                    g.dispose();

                    BufferStrategy strategy = getBufferStrategy();
                    if (!strategy.contentsLost()) {
                        strategy.show();
                    }

                    Wrapper.getTimer().updateFrames();
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    private void shutdown() {
        this.programController.shutdown();
        Wrapper.getProcessManager().shutdown();
        try {
            if (!Wrapper.getProcessManager().getServicesExecutor().awaitTermination(800, TimeUnit.MILLISECONDS)) {
                Wrapper.getProcessManager().getServicesExecutor().shutdownNow();
            }
        } catch (InterruptedException e) {
            Wrapper.getProcessManager().getServicesExecutor().shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }

    public boolean watchPhysics() {
        return this.watchPhysics.get();
    }

    public void setWatchPhyics(boolean flag) {
        this.watchPhysics.set(flag);
    }

    public void continueStart() {
        this.initializing.set(false);
    }

    private void createWindow(){
        this.setBackground(Color.decode("#d0b99c"));
        logger.info("Creating Window...");

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = env.getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int x = width / 2 - rise_of_duebel.Config.WINDOW_WIDTH / 2;
        int y = height / 2 - rise_of_duebel.Config.WINDOW_HEIGHT / 2;
        logger.info("Graphics Device: {}", gd.getIDstring());

        if (rise_of_duebel.Config.RUN_ENV == rise_of_duebel.Config.Environment.PRODUCTION) {
            Scene.open(new LoadingScene());
        }
        this.drawFrame = new DrawFrame(rise_of_duebel.Config.WINDOW_TITLE, x, y, rise_of_duebel.Config.WINDOW_WIDTH, rise_of_duebel.Config.WINDOW_HEIGHT, this);
        this.drawFrame.setResizable(false);

        if (rise_of_duebel.Config.RUN_ENV == rise_of_duebel.Config.Environment.PRODUCTION) {
            this.drawFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {
                    shutdown();
                }
                @Override
                public void windowClosed(WindowEvent e) {}
                @Override
                public void windowIconified(WindowEvent e) {}
                @Override
                public void windowDeiconified(WindowEvent e) {}
                @Override
                public void windowActivated(WindowEvent e) {}
                @Override
                public void windowDeactivated(WindowEvent e) {}
            });
        }
        this.drawFrame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {}
            @Override
            public void windowLostFocus(WindowEvent e) {
                currentlyPressedKeys.clear();
                currentlyPressedMouseButtons.clear();
            }
        });

        if (rise_of_duebel.Config.WINDOW_FULLSCREEN) {
            this.drawFrame.setUndecorated(true);
            this.drawFrame.setVisible(true);
            this.drawFrame.setResizable(false);
            this.drawFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        } else {
            this.drawFrame.setVisible(true);
        }

        this.addMouseListener(this);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();

        if (rise_of_duebel.Config.RUN_ENV == rise_of_duebel.Config.Environment.DEVELOPMENT) {
            Scene.open(GameScene.getInstance());
        }
    }

    /**
     * Diese Methode überprüft, ob die angebene Taste momentan heruntergedrückt ist.
     * @param key Der Tastecode der zu überprüfenden Taste.
     * @return True, falls die entsprechende Taste momentan gedrückt ist, andernfalls false.
     */
    public static boolean isKeyDown(int key){
        return ViewController.currentlyPressedKeys.contains(key);
    }

    /**
     * Diese Methode überprüft, ob die angebene Taste momentan heruntergedrückt ist.
     * @param key Der Tastecode der zu überprüfenden Taste.
     * @return True, falls die entsprechende Taste momentan gedrückt ist, andernfalls false.
     */
    public static boolean isMouseDown(int key){
        return ViewController.currentlyPressedMouseButtons.contains(key);
    }

    /**
     * Nötig zur Einbindung nativer Java-Fensterelemente
     * @return Liefert das DrawFrame-Objekt zurück (als Schnittstelle zu den JFrame-Methoden)
     */
    public DrawFrame getDrawFrame(){
        return this.drawFrame;
    }

    /**
     * Zeigt das Standardfenster an oder versteckt es.
     * @param b der gewünschte Sichtbarkeitsstatus
     */
    public void setDrawFrameVisible(boolean b){
        drawFrame.setVisible(b);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseExited(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!ViewController.currentlyPressedMouseButtons.contains(e.getButton()))
            ViewController.currentlyPressedMouseButtons.add(e.getButton());
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseReleased(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ViewController.currentlyPressedMouseButtons.remove(Integer.valueOf(e.getButton()));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseClicked(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseMoved(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mousePressed(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!ViewController.currentlyPressedKeys.contains(e.getKeyCode()))
            ViewController.currentlyPressedKeys.add(e.getKeyCode());
        Wrapper.getEventManager().dispatchEvent(new KeyPressedEvent(e));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentlyPressedKeys.remove(Integer.valueOf(e.getKeyCode()));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyReleased(e);
        }
    }

    public ProgramController getProgramController() {
        return this.programController;
    }
}