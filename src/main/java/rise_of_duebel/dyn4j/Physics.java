package rise_of_duebel.dyn4j;

import KAGO_framework.control.ViewController;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.StepListenerAdapter;
import rise_of_duebel.Wrapper;
import rise_of_duebel.utils.input.BooleanStateKeyboardInputHandler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public final class Physics {

    private final ViewController canvas;

    private final BooleanStateKeyboardInputHandler up;
    private final BooleanStateKeyboardInputHandler down;
    private final BooleanStateKeyboardInputHandler left;
    private final BooleanStateKeyboardInputHandler right;

    private final World<ColliderBody> world;
    private final ColliderBody character;

    private boolean onGround = false;

    public Physics() {
        this.canvas = ViewController.getInstance();

        this.up = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_UP);
        this.down = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_DOWN);
        this.left = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_LEFT);
        this.right = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_RIGHT);

        this.up.install();
        this.down.install();
        this.left.install();
        this.right.install();

        this.world = new World<ColliderBody>();

        ColliderBody floor = new ColliderBody();
        floor.addFixture(Geometry.createRectangle(50.0, 0.2));
        floor.setMass(MassType.INFINITE);
        floor.translate(0, -3);
        floor.setUserData(ColliderData.FLOOR);
        this.world.addBody(floor);

        ColliderBody platform = new ColliderBody();
        platform.addFixture(Geometry.createRectangle(10.0, 0.2));
        platform.setMass(MassType.INFINITE);
        platform.translate(0, 0);
        platform.setUserData(ColliderData.ONE_WAY_PLATFORM);
        this.world.addBody(platform);

        this.character = new ColliderBody(Color.MAGENTA);
        // NOTE: lots of friction to simulate a sticky tire
        this.character.addFixture(Geometry.createCapsule(8, 10), 1.0, 20.0, 0.1);
        this.character.setMass(MassType.NORMAL);
        this.character.translate(0.0, -2.0);
        this.character.setUserData(ColliderData.CHARACTER);
        this.character.setAtRestDetectionEnabled(false);
        this.world.addBody(character);

        this.world.addStepListener(new StepListenerAdapter<>() {
            @Override
            public void begin(TimeStep step, PhysicsWorld<ColliderBody, ?> world) {
                super.begin(step, world);

                boolean isGround = false;
                List<ContactConstraint<ColliderBody>> contacts = world.getContacts(character);
                for (ContactConstraint<ColliderBody> cc : contacts) {
                    if (is(cc.getOtherBody(character), ColliderData.FLOOR, ColliderData.ONE_WAY_PLATFORM) && cc.isEnabled()) {
                        isGround = true;
                    }
                }

                // only clear it
                if (!isGround) {
                    onGround = false;
                }
            }
        });

        this.world.addContactListener(new ContactListenerAdapter<ColliderBody>() {
            @Override
            public void collision(ContactCollisionData<ColliderBody> collision) {
                ContactConstraint<ColliderBody> cc = collision.getContactConstraint();

                // set the other body to one-way if necessary
                disableContactForOneWay(cc);

                // track on the on-ground status
                trackIsOnGround(cc);

                super.collision(collision);
            }
        });
    }

    /**
     * Helper method to determine if a body is one of the given types assuming
     * the type is stored in the user data.
     * @param body the body
     * @param types the set of types
     * @return boolean
     */
    private boolean is(ColliderBody body, Object... types) {
        for (Object type : types) {
            if (body.getUserData() == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given platform should be toggled as one-way
     * given the position of the character body.
     * @param character the character body
     * @param platform the platform body
     * @return boolean
     */
    private boolean allowOneWayUp(ColliderBody character, ColliderBody platform) {
        AABB wAABB = character.createAABB();
        AABB pAABB = platform.createAABB();

        // NOTE: this would need to change based on the shape of the platform and it's orientation
        //
        // one thought might be to store the allowed normal of the platform on the platform body
        // and check that against the ContactConstraint normal to see if they are pointing in the
        // same direction
        //
        // another option might be to project both onto the platform normal to see where they are overlapping
        if (wAABB.getMinY() < pAABB.getMinY()) {
            return true;
        }
        return false;
    }

    /**
     * Disables the constraint if it's between the character and platform and it
     * the scenario meets the condition for one-way.
     * @param contactConstraint the constraint
     */
    private void disableContactForOneWay(ContactConstraint<ColliderBody> contactConstraint) {
        ColliderBody b1 = contactConstraint.getBody1();
        ColliderBody b2 = contactConstraint.getBody2();

        if (is(b1, ColliderData.CHARACTER) && is(b2, ColliderData.ONE_WAY_PLATFORM)) {
            if (allowOneWayUp(b1, b2) || down.isActiveButNotHandled()) {
                down.setHasBeenHandled(true);
                contactConstraint.setEnabled(false);
            }
        } else if (is(b1, ColliderData.ONE_WAY_PLATFORM) && is(b2, ColliderData.CHARACTER)) {
            if (allowOneWayUp(b2, b1) || down.isActiveButNotHandled()) {
                down.setHasBeenHandled(true);
                contactConstraint.setEnabled(false);
            }
        }
    }

    public void handleEvents() {
        // apply a torque based on key input
        if (this.left.isActive()) {
            character.applyTorque(Math.PI / 2);
        }
        if (this.right.isActive()) {
            character.applyTorque(-Math.PI / 2);
        }

        // only allow jumping if the body is on the ground
        if (this.up.isActiveButNotHandled()) {
            this.up.setHasBeenHandled(true);
            if (this.onGround) {
                character.applyImpulse(new Vector2(0.0, 7));
            }
        }

        // color the body green if it's on the ground
        if (this.onGround) {
            character.setColor(WHEEL_ON_COLOR);
        } else {
            character.setColor(WHEEL_OFF_COLOR);
        }
    }

    /**
     * Sets the isOnGround flag if the given contact constraint is between
     * the character body and a floor or one-way platform.
     * @param contactConstraint
     */
    private void trackIsOnGround(ContactConstraint<ColliderBody> contactConstraint) {
        ColliderBody b1 = contactConstraint.getBody1();
        ColliderBody b2 = contactConstraint.getBody2();

        if (is(b1, ColliderData.CHARACTER) &&
                is(b2, ColliderData.FLOOR, ColliderData.ONE_WAY_PLATFORM) &&
                contactConstraint.isEnabled()) {
            this.onGround = true;

        } else if (is(b1, ColliderData.FLOOR, ColliderData.ONE_WAY_PLATFORM) &&
                is(b2, ColliderData.CHARACTER) &&
                contactConstraint.isEnabled()) {
            this.onGround = true;
        }
    }

    public World<ColliderBody> getWorld() {
        return this.world;
    }
}
