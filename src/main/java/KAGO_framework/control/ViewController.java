package KAGO_framework.control;

import KAGO_framework.Config;
import KAGO_framework.view.DrawFrame;
import KAGO_framework.view.DrawTool;
import duebel_level.ProgramController;
import duebel_level.Wrapper;
import duebel_level.event.events.KeyPressedEvent;
import duebel_level.model.scene.Scene;
import duebel_level.model.scene.impl.LoadingScene;
import duebel_level.model.transitions.DefaultTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private int screenWidth;
    private int screenHeight;
    private int mouseX;
    private int mouseY;

    /**
     * Initialisiert ProgramController, Fenster und startet die Engine.
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

        if (!duebel_level.Config.SHOW_DEFAULT_WINDOW) {
            if(Config.INFO_MESSAGES) System.out.println("** Achtung! Standardfenster deaktiviert => wird nicht angezeigt.). **");
        }

        this.startProgram();
    }

    /**
     * @return Singleton-Instanz
     */
    public static ViewController getInstance() {
        return instance;
    }

    /** Startet Engine-Setup nach PreStart. */
    private void startProgram() {
        try {
            this.setWatchPhyics(false);
            this.startGameEngine();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Startet den Game-Loop (Update/Draw) und initialisiert BufferStrategy.
     *
     * @throws InterruptedException wenn Start unterbrochen wird
     */
    private void startGameEngine() throws InterruptedException {
        this.programController.startProgram();
        this.setWatchPhyics(true);
        Wrapper.getProcessManager().processPostGame();

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
                    Scene.updateAll(dt);

                    drawTool.setGraphics2D(g);
                    if (Scene.getCurrentScene() != null) Scene.getCurrentScene().draw(drawTool);
                    Scene.drawTransition(drawTool);

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

    /** Beendet Programm und Services kontrolliert und ruft System.exit. */
    public void shutdown() {
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

    /**
     * @return aktueller Physics-Watch Status
     */
    public boolean watchPhysics() {
        return this.watchPhysics.get();
    }

    /**
     * Setzt Physics-Watch Status.
     *
     * @param flag Flag
     */
    public void setWatchPhyics(boolean flag) {
        this.watchPhysics.set(flag);
    }

    /** Signalisiert das Ende der PreStart-Initialisierung. */
    public void continueStart() {
        this.initializing.set(false);
    }

    /**
     * @return Bildschirmbreite
     */
    public int getScreenWidth() {
        return this.screenWidth;
    }

    /**
     * @return Bildschirmhöhe
     */
    public int getScreenHeight() {
        return this.screenHeight;
    }

    /** Erstellt das Fenster, registriert Listener und initialisiert Start-Scene. */
    private void createWindow(){
        this.setBackground(Color.decode("#d0b99c"));
        logger.info("Creating Window...");

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = env.getDefaultScreenDevice();
        this.screenWidth = gd.getDisplayMode().getWidth();
        this.screenHeight = gd.getDisplayMode().getHeight();
        int x = this.screenWidth / 2 - duebel_level.Config.WINDOW_WIDTH / 2;
        int y = this.screenHeight / 2 - duebel_level.Config.WINDOW_HEIGHT / 2;
        logger.info("Graphics Device: {}", gd.getIDstring());

        if (duebel_level.Config.RUN_ENV == duebel_level.Config.Environment.PRODUCTION) {
            Scene.open(new LoadingScene(), new DefaultTransition());
        }
        this.drawFrame = new DrawFrame(duebel_level.Config.WINDOW_TITLE, x, y, duebel_level.Config.WINDOW_WIDTH, duebel_level.Config.WINDOW_HEIGHT, this);
        this.drawFrame.setResizable(false);

        if (duebel_level.Config.RUN_ENV == duebel_level.Config.Environment.PRODUCTION) {
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

        if (duebel_level.Config.WINDOW_FULLSCREEN) {
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

        if (duebel_level.Config.RUN_ENV == duebel_level.Config.Environment.DEVELOPMENT) {
            Scene.showGameScene();
        }
    }

    /**
     * Prüft, ob eine Taste aktuell gedrückt ist.
     *
     * @param key KeyCode
     * @return true, wenn gedrückt
     */
    public static boolean isKeyDown(int key){
        return ViewController.currentlyPressedKeys.contains(key);
    }

    /**
     * Prüft, ob ein Mausbutton aktuell gedrückt ist.
     *
     * @param key Button-Code
     * @return true, wenn gedrückt
     */
    public static boolean isMouseDown(int key){
        return ViewController.currentlyPressedMouseButtons.contains(key);
    }

    /**
     * @return DrawFrame (Fenster) der Engine
     */
    public DrawFrame getDrawFrame(){
        return this.drawFrame;
    }

    /**
     * Setzt Sichtbarkeit des Fensters.
     *
     * @param b sichtbar
     */
    public void setDrawFrameVisible(boolean b){
        drawFrame.setVisible(b);
    }

    /** Leitet Mouse-Event an die aktuelle Scene weiter. */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseEntered(e);
        }
    }

    /** Leitet Mouse-Event an die aktuelle Scene weiter. */
    @Override
    public void mouseExited(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseExited(e);
        }
    }

    /** Speichert Button-Status und leitet Event weiter. */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!ViewController.currentlyPressedMouseButtons.contains(e.getButton()))
            ViewController.currentlyPressedMouseButtons.add(e.getButton());
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseReleased(e);
        }
    }

    /** Entfernt Button-Status und leitet Event weiter. */
    @Override
    public void mouseClicked(MouseEvent e) {
        ViewController.currentlyPressedMouseButtons.remove(Integer.valueOf(e.getButton()));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseClicked(e);
        }
    }

    /** Leitet Mouse-Event an die aktuelle Scene weiter. */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mouseDragged(e);
        }
    }

    /** Aktualisiert Mausposition und leitet Event weiter. */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            this.mouseX = e.getX();
            this.mouseY = e.getY();
            Scene.getCurrentScene().mouseMoved(e);
        }
    }

    /** Leitet Mouse-Event an die aktuelle Scene weiter. */
    @Override
    public void mousePressed(MouseEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().mousePressed(e);
        }
    }

    /** Leitet Key-Event an die aktuelle Scene weiter. */
    @Override
    public void keyTyped(KeyEvent e) {
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyTyped(e);
        }
    }

    /** Speichert Key-Status, dispatcht Event und leitet an Scene weiter. */
    @Override
    public void keyPressed(KeyEvent e) {
        if (!ViewController.currentlyPressedKeys.contains(e.getKeyCode()))
            ViewController.currentlyPressedKeys.add(e.getKeyCode());
        Wrapper.getEventManager().dispatchEvent(new KeyPressedEvent(e));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyPressed(e);
        }
    }

    /** Entfernt Key-Status und leitet an Scene weiter. */
    @Override
    public void keyReleased(KeyEvent e) {
        currentlyPressedKeys.remove(Integer.valueOf(e.getKeyCode()));
        if (Scene.getCurrentScene() != null) {
            Scene.getCurrentScene().keyReleased(e);
        }
    }

    /**
     * @return Maus-X im Fenster
     */
    public int getMouseX() {
        return this.mouseX;
    }

    /**
     * @return Maus-Y im Fenster
     */
    public int getMouseY() {
        return this.mouseY;
    }

    /**
     * @return ProgramController Instanz
     */
    public ProgramController getProgramController() {
        return this.programController;
    }
}