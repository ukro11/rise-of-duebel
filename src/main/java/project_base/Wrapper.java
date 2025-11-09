package project_base;

import KAGO_framework.control.ViewController;
import project_base.event.EventManager;
import project_base.event.services.EventProcessingQueue;
import project_base.graphics.map.MapManager;
import project_base.graphics.tooltip.TooltipManager;
import project_base.model.GameHandlerModel;
import project_base.model.entity.EntityManager;
import project_base.model.entity.impl.player.EntityPlayer;
import project_base.model.sound.SoundConstants;
import project_base.physics.ColliderManager;
import project_base.utils.TimerUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Wrapper {

    private final static EventManager eventManager = new EventManager();
    private final static ColliderManager colliderManager = new ColliderManager();
    private final static EntityManager entityManager = new EntityManager();
    private final static EventProcessingQueue processManager = new EventProcessingQueue();
    private final static TooltipManager tooltipManager = new TooltipManager();
    private final static GameHandlerModel gameHandlerModel = new GameHandlerModel();
    private final static SoundConstants soundConstants = new SoundConstants();
    private final static TimerUtils timer = new TimerUtils();
    private final static TimerUtils physicsTimer = new TimerUtils();
    private final static MapManager mapManager = new MapManager();

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static ColliderManager getColliderManager() {
        return colliderManager;
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static EventProcessingQueue getProcessManager() { return processManager; }

    public static TooltipManager getTooltipManager() { return tooltipManager; }

    public static EntityPlayer getLocalPlayer() { return ViewController.getInstance().getProgramController().player; }

    public static SoundConstants getSoundConstants() { return soundConstants; }

    public static TimerUtils getTimer() { return timer; }

    public static TimerUtils getPhysicsTimer() { return physicsTimer; }

    public static GameHandlerModel getGameHandler() { return gameHandlerModel; }

    public static MapManager getMapManager() { return mapManager; }

    public static BufferedImage getImage(String src) {
        try {
            return ImageIO.read(Wrapper.class.getResourceAsStream(src));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
