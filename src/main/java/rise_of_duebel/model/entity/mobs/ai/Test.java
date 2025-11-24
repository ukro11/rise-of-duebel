package rise_of_duebel.model.entity.mobs.ai;

import KAGO_framework.control.ViewController;
import rise_of_duebel.Wrapper;
import rise_of_duebel.model.entity.mobs.EntityMob;
import rise_of_duebel.model.entity.player.EntityPlayer;

import java.awt.event.KeyEvent;

public class Test extends Goal {

    private final EntityPlayer player;

    public Test(EntityMob<?> mob) {
        super(mob, true);
        this.player = Wrapper.getLocalPlayer();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void update(double dt) {
        if (ViewController.getInstance().isKeyDown(KeyEvent.VK_Y)) {
            this.mob.getBody().setPosition(this.player.getX() + 10, this.player.getY() + 10);
        }
    }

    @Override
    public boolean trigger() {
        return Wrapper.getLocalPlayer().getY() > 100;
    }
}
