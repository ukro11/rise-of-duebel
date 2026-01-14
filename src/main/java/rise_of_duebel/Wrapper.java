package rise_of_duebel;

import KAGO_framework.control.ViewController;
import rise_of_duebel.event.EventManager;
import rise_of_duebel.event.services.EventProcessingQueue;
import rise_of_duebel.graphics.level.LevelManager;
import rise_of_duebel.graphics.tooltip.TooltipManager;
import rise_of_duebel.model.entity.EntityManager;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.sound.SoundConstants;
import rise_of_duebel.model.user.UserProfile;
import rise_of_duebel.utils.TimerUtils;

public class Wrapper {

    private final static UserProfile userProfile = new UserProfile();
    private final static EventManager eventManager = new EventManager();
    private final static EntityManager entityManager = new EntityManager();
    private final static EventProcessingQueue processManager = new EventProcessingQueue();
    private final static TooltipManager tooltipManager = new TooltipManager();
    private final static SoundConstants soundConstants = new SoundConstants();
    private final static TimerUtils timer = new TimerUtils();
    private final static TimerUtils physicsTimer = new TimerUtils();
    private final static LevelManager levelManager = new LevelManager(1);

    public static UserProfile getUserProfile() {
        return userProfile;
    }

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

    public static TimerUtils getPhysicsTimer() { return physicsTimer; }

    public static ViewController getViewController() { return ViewController.getInstance(); }

    public static ProgramController getProgramController() { return ViewController.getInstance().getProgramController(); }

    public static int getScreenWidth() { return ViewController.getInstance().getScreenWidth(); }

    public static int getScreenHeight() { return ViewController.getInstance().getScreenHeight(); }

    public static LevelManager getLevelManager() { return levelManager; }
}
