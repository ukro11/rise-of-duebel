package duebel_level;

import KAGO_framework.control.ViewController;
import duebel_level.event.EventManager;
import duebel_level.event.services.EventProcessingQueue;
import duebel_level.graphics.level.LevelManager;
import duebel_level.graphics.tooltip.TooltipManager;
import duebel_level.model.entity.EntityManager;
import duebel_level.model.entity.impl.EntityPlayer;
import duebel_level.model.sound.SoundConstants;
import duebel_level.utils.TimerUtils;

public class Wrapper {

    private final static EventManager eventManager = new EventManager();
    private final static EntityManager entityManager = new EntityManager();
    private final static EventProcessingQueue processManager = new EventProcessingQueue();
    private final static TooltipManager tooltipManager = new TooltipManager();
    private final static SoundConstants soundConstants = new SoundConstants();
    private final static TimerUtils timer = new TimerUtils();
    private final static LevelManager levelManager = new LevelManager(9);

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static EventProcessingQueue getProcessManager() { return processManager; }

    public static TooltipManager getTooltipManager() { return tooltipManager; }

    public static EntityPlayer getLocalPlayer() { return getProgramController().player; }

    public static SoundConstants getSoundConstants() { return soundConstants; }

    public static TimerUtils getTimer() { return timer; }

    public static ViewController getViewController() { return ViewController.getInstance(); }

    public static ProgramController getProgramController() { return ViewController.getInstance().getProgramController(); }

    public static int getScreenWidth() { return ViewController.getInstance().getScreenWidth(); }

    public static int getScreenHeight() { return ViewController.getInstance().getScreenHeight(); }

    public static LevelManager getLevelManager() { return levelManager; }
}
