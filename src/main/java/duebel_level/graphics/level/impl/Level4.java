package duebel_level.graphics.level.impl;

import duebel_level.animation.Easings;
import duebel_level.animation.tween.Tween;
import duebel_level.dyn4j.ColliderBody;
import duebel_level.dyn4j.PhysicsUtils;
import duebel_level.dyn4j.WorldCollider;
import duebel_level.graphics.level.LevelColors;
import duebel_level.graphics.level.LevelLoader;
import duebel_level.graphics.level.LevelMap;
import duebel_level.model.entity.impl.EntityPlayer;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.listener.ContactListenerAdapter;

public class Level4 extends LevelLoader{

    private WorldCollider moving;
    private WorldCollider sensor;

    private Tween TWEEN_COLLIDER_MOVING_RIGHT;
    private double TWEEN_START_COLLIDER_MOVING_RIGHT = 0.0;
    private Vector2 TWEEN_SAVED_VALUE_COLLIDER_MOVING_RIGHT;
    private boolean start = false;

    public Level4(LevelMap map) {
        super("level4.json", LevelColors.createDefault(), map);
        this.moving = this.map.getColliderByLayer("MOVING", 0);
        this.sensor = this.moving.getSensorByIndex(0);
        BodyFixture sensorFixture = this.sensor.getFixture();

        this.TWEEN_COLLIDER_MOVING_RIGHT = Tween.to(this.TWEEN_START_COLLIDER_MOVING_RIGHT, this.moving.getWidth(), 0.3)
                .ease((x) -> Easings.easeOutBounce(x))
                .loop(false);

        TWEEN_SAVED_VALUE_COLLIDER_MOVING_RIGHT = new Vector2(moving.getTransform().getTranslationX(), moving.getTransform().getTranslationY());

        this.world.addContactListener(new ContactListenerAdapter<>() {
            @Override
            public void begin(ContactCollisionData<ColliderBody> collision, Contact contact) {
                BodyFixture body1 = collision.getFixture1();
                BodyFixture body2 = collision.getFixture2();
                if (PhysicsUtils.is(sensorFixture, body1, body2) && EntityPlayer.containsPlayer(body1, body2)) {
                    if (!start && !TWEEN_COLLIDER_MOVING_RIGHT.isRunning()) {
                        TWEEN_COLLIDER_MOVING_RIGHT.animate();
                        TWEEN_COLLIDER_MOVING_RIGHT.onFinish((t) -> {
                            TWEEN_COLLIDER_MOVING_RIGHT.redo(TWEEN_COLLIDER_MOVING_RIGHT.getValueDouble(), TWEEN_START_COLLIDER_MOVING_RIGHT, 0.5).delay(1);
                            TWEEN_COLLIDER_MOVING_RIGHT.animate();
                        });
                        start = true;
                    }
                }
            }
        });
    }

    @Override
    public void resetLevel() {
        this.start = false;
        this.TWEEN_COLLIDER_MOVING_RIGHT.stop();
        this.TWEEN_COLLIDER_MOVING_RIGHT = Tween.to(this.TWEEN_START_COLLIDER_MOVING_RIGHT, this.moving.getWidth(), 0.3)
                .ease((x) -> Easings.easeOutBounce(x))
                .loop(false);
    }

    @Override
    public void updateCollider(TimeStep step, PhysicsWorld<ColliderBody, ?> world) {
        moving.getTransform().setTranslation(TWEEN_SAVED_VALUE_COLLIDER_MOVING_RIGHT.x + TWEEN_COLLIDER_MOVING_RIGHT.getValueDouble(), TWEEN_SAVED_VALUE_COLLIDER_MOVING_RIGHT.y);
    }

    @Override
    public void enterPortal() {

    }
}
