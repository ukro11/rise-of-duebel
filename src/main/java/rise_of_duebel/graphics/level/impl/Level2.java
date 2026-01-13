package rise_of_duebel.graphics.level.impl;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.listener.ContactListenerAdapter;
import rise_of_duebel.animation.Easings;
import rise_of_duebel.animation.tween.Tween;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.dyn4j.PhysicsUtils;
import rise_of_duebel.dyn4j.WorldCollider;
import rise_of_duebel.graphics.level.LevelColors;
import rise_of_duebel.graphics.level.LevelLoader;
import rise_of_duebel.graphics.level.LevelMap;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.scene.Scene;
import rise_of_duebel.model.scene.impl.WinScene;
import rise_of_duebel.model.transitions.DefaultTransition;

public class Level2 extends LevelLoader {

    private WorldCollider moving;
    private WorldCollider sensor;

    private Tween TWEEN_COLLIDER_MOVING_RIGHT;
    private double TWEEN_START_COLLIDER_MOVING_RIGHT = 0.0;
    private Vector2 TWEEN_SAVED_VALUE_COLLIDER_MOVING_RIGHT;
    private boolean start = false;

    public Level2(LevelMap map) {
        super("level2.json", new LevelColors("#f4b13b", "#be7708", "#be7708", "#6603fc"), map);
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
