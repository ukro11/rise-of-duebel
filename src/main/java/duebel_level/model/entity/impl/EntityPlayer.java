package duebel_level.model.entity.impl;

import KAGO_framework.control.ViewController;
import KAGO_framework.model.abitur.datenstrukturen.Queue;
import KAGO_framework.view.DrawTool;
import duebel_level.Wrapper;
import duebel_level.animation.AnimationRenderer;
import duebel_level.animation.entity.EntityState;
import duebel_level.animation.states.CharacterAnimationState;
import duebel_level.dyn4j.ColliderBody;
import duebel_level.dyn4j.PhysicsUtils;
import duebel_level.event.services.EventProcessCallback;
import duebel_level.event.services.process.EventLoadAssetsProcess;
import duebel_level.model.entity.Entity;
import duebel_level.model.entity.EntityDirection;
import duebel_level.model.sound.SoundManager;
import duebel_level.model.user.UserProfile;
import duebel_level.utils.CooldownManager;
import duebel_level.utils.MathUtils;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.StepListenerAdapter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

/***
 * @author Mark
 */
public class EntityPlayer extends Entity<CharacterAnimationState> {

    private EntityDirection direction = EntityDirection.RIGHT;
    private EntityDirection lastDirection = null;

    private boolean freeze = false;
    private boolean freezePermanent = false;
    private CooldownManager freezeCooldown;
    private Queue<Double> freezeQueue;
    private boolean visible = true;

    private boolean onGround = false;

    private final static double MOVE_SPEED = 245.0;
    private final static double JUMP_FORCE = -360.0;
    private final static double AIR_CONTROL = 0.5;

    private List<Consumer<EntityPlayer>> onDirectionChange = new ArrayList<>();

    private final UserProfile userProfile;

    /**
     * Erstellt den Player inkl. Collider, Fußsensor, Animations-Loading und Ground-Detection.
     *
     * @param world Physics-World
     * @param x Start-X
     * @param y Start-Y
     * @param width Render-Breite
     * @param height Render-Höhe
     */
    public EntityPlayer(World<ColliderBody> world, double x, double y, double width, double height) {
        super(world, new ColliderBody(Color.GREEN), x, y, width, height);
        this.id = String.format("ENTITY_PLAYER_%s", UUID.randomUUID());
        this.userProfile = new UserProfile(this);

        double colliderWidth = 8.0;
        double colliderHeight = 10.0;
        var fixture = this.body.addFixture(Geometry.createCapsule(colliderWidth, colliderHeight));
        fixture.setUserData(this.id);
        fixture.setFriction(0.0);
        fixture.setFilter(new CategoryFilter(ColliderBody.MASK_ENTITY_PLAYER, ColliderBody.FILTER_DEFAULT));

        var center = this.getBody().getLocalCenter();
        var footVertices = new Vector2[] {
                new Vector2(center.x - 1, colliderHeight / 2 - 2),
                new Vector2(center.x + 1, colliderHeight / 2 - 2),
                new Vector2(center.x + 1, colliderHeight / 2),
                new Vector2(center.x - 1, 0 + colliderHeight / 2),
        };
        var foot = this.body.addFixture(Geometry.createPolygon(footVertices));
        foot.setUserData(String.format("FOOT_%s", this.id));
        foot.setSensor(true);

        this.body.setMass(MassType.FIXED_ANGULAR_VELOCITY);
        this.body.translate(x, y);
        this.body.setUserData(this.id);
        this.body.setAngularVelocity(0.0);
        this.body.setAngularDamping(0.0);
        this.body.setAtRestDetectionEnabled(false);
        this.world.addBody(this.body);

        this.freezeQueue = new Queue<>();

        Wrapper.getProcessManager().queue(new EventLoadAssetsProcess<AnimationRenderer>("Loading animations", () -> new AnimationRenderer(
                "/graphic/character/player/player.png", 9, 12, 192, 128,
                CharacterAnimationState.IDLE_RIGHT
        ), new EventProcessCallback<AnimationRenderer>() {
            @Override
            public void onSuccess(AnimationRenderer data) {
                setRenderer(data);
            }
        }));

        this.world.addStepListener(new StepListenerAdapter<>() {
            @Override
            public void begin(TimeStep step, PhysicsWorld<ColliderBody, ?> world) {
                super.begin(step, world);

                boolean s = false;
                List<ContactConstraint<ColliderBody>> contacts = world.getContacts(body);
                for (ContactConstraint<ColliderBody> cc : contacts) {
                    if (PhysicsUtils.isGround(cc.getOtherBody(body)) && PhysicsUtils.is(body.getFixture(1), cc.getFixture1(), cc.getFixture2()) && cc.isEnabled()) {
                        s = true;
                        onGround = true;
                    }
                }

                // only clear it
                if (!s) {
                    onGround = false;
                }
            }
        });
    }

    /**
     * Aktualisiert Freeze-Logik, UserProfile und Bewegung (wenn Renderer geladen).
     *
     * @param dt delta time
     */
    @Override
    public void update(double dt) {
        super.update(dt);
        if (!this.freezePermanent) {
            if (!this.freezeQueue.isEmpty() && this.freezeCooldown == null) {
                this.freeze = true;
                this.freezeCooldown = new CooldownManager(this.freezeQueue.front());
                this.freezeQueue.dequeue();
                this.freezeCooldown.use();
            }
            if (this.freezeCooldown != null && this.freezeCooldown.use()) {
                this.freeze = false;
                this.freezeCooldown = null;
            }
        }
        if (this.renderer != null) {
            this.userProfile.update(dt);
            if (this.freeze && !this.isWalking()) return;
            this.onMove();
        }
    }

    /**
     * Setzt/entfernt permanenten Freeze und leert die Freeze-Queue.
     *
     * @param flag true = einfrieren, false = freigeben
     */
    public void freezeInfinity(boolean flag) {
        this.freezePermanent = true;
        this.freezeCooldown = null;
        while (!this.freezeQueue.isEmpty()) this.freezeQueue.dequeue();
        this.freeze = flag;
    }

    /**
     * Fügt einen zeitbegrenzten Freeze in die Queue ein.
     *
     * @param time Dauer in Sekunden
     */
    public void freeze(double time) {
        this.freezePermanent = false;
        this.freezeQueue.enqueue(time);
    }

    /**
     * @return true, wenn aktuell eingefroren
     */
    public boolean isFreeze() {
        return this.freeze;
    }

    /** Verarbeitet Bewegung/Sprung und wechselt Animationszustände. */
    private void onMove() {
        double SPEED = MOVE_SPEED;
        if (!this.viewController.getDrawFrame().isFocused() || this.freeze) {
            // ONLY LOCAL PLAYER
            SPEED = 0;
        }

        double targetSpeed = 0;
        Vector2 vel = this.body.getLinearVelocity();

        if (ViewController.isKeyDown(KeyEvent.VK_A)) targetSpeed -= SPEED;
        if (ViewController.isKeyDown(KeyEvent.VK_D)) targetSpeed += SPEED;

        double control = this.onGround ? 1.0 : AIR_CONTROL;
        double newX = MathUtils.lerp(vel.x, targetSpeed, control);

        this.body.setLinearVelocity(newX, vel.y);
        this.setX(Math.floor(this.body.getX()));
        this.setY(Math.floor(this.body.getY()));

        if (ViewController.isKeyDown(KeyEvent.VK_SPACE) && this.onGround) {
            Wrapper.getSoundConstants().SOUND_JUMP.setVolume(0.75);
            SoundManager.playSound(Wrapper.getSoundConstants().SOUND_JUMP, false);
            this.body.setLinearVelocity(vel.x, JUMP_FORCE);
            this.onGround = false;
            this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.IDLE));
        }

        if (this.onGround) {
            if (this.body.getLinearVelocity().x == 0) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.IDLE));

            } else {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.WALKING));
            }
        } else {
            if (this.body.getLinearVelocity().y > 0) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.JUMP_UP));

            } else {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.JUMP_DOWN));
            }
        }
    }

    /**
     * Zeichnet den aktuellen Animationsframe (optional gespiegelt bei LEFT).
     *
     * @param drawTool DrawTool
     */
    @Override
    protected void drawEntity(DrawTool drawTool) {
        if (this.renderer != null && this.renderer.getCurrentFrame() != null && this.isVisible()) {
            drawTool.push();
            if (this.direction == EntityDirection.LEFT) {
                double centerX = this.getX();
                double centerY = this.getY() + this.height / 2;

                drawTool.getGraphics2D().translate(centerX, centerY);
                drawTool.getGraphics2D().scale(-1, 1);
                drawTool.getGraphics2D().translate(-centerX, -centerY);
            }

            drawTool.getGraphics2D().drawImage(
                    this.renderer.getCurrentFrame(),
                    (int) this.getX() - 58,
                    (int) this.getY() - (int) this.height / 2 - 13 + 2,
                    (int) this.width,
                    (int) this.height,
                    null
            );
            drawTool.pop();
        }
    }

    /**
     * @return zIndex über Basis-Entity (Player vor anderen Entities)
     */
    @Override
    public double zIndex() {
        return super.zIndex() + 5;
    }

    /**
     * Sammelt alle AnimationStates, die zu Richtung und EntityState passen.
     *
     * @param direction Richtung
     * @param state EntityState
     * @return Liste passender AnimationStates
     */
    private List<CharacterAnimationState> getStatesForEntityState(EntityDirection direction, EntityState state) {
        List<CharacterAnimationState> s = List.of();
        for (CharacterAnimationState anim : CharacterAnimationState.values()) {
            if (anim.getDirection() == direction && anim.getState() == state) {
                s.add(anim);
            }
        }
        return s;
    }

    /**
     * @param direction Richtung
     * @param state EntityState
     * @return erster passender AnimationState oder null
     */
    private CharacterAnimationState getStateForEntityState(EntityDirection direction, EntityState state) {
        for (CharacterAnimationState anim : CharacterAnimationState.values()) {
            if (anim.getDirection() == direction && anim.getState() == state) {
                return anim;
            }
        }
        return null;
    }

    /**
     * @param state EntityState
     * @return true, wenn aktuelle Animation diesen State hat
     */
    private boolean isCurrentAnimation(EntityState state) {
        var anim = this.renderer.getCurrentAnimation().getState();
        return anim.getState() == state;
    }

    /**
     * @param direction Richtung
     * @param state EntityState
     * @return true, wenn aktuelle Animation Richtung und State matcht
     */
    private boolean isCurrentAnimation(EntityDirection direction, EntityState state) {
        var anim = this.renderer.getCurrentAnimation().getState();
        return anim.getDirection() == direction && anim.getState() == state;
    }

    /**
     * Setzt die Body-Position direkt per Transform.
     *
     * @param x Ziel-X
     * @param y Ziel-Y
     */
    public void setPosition(double x, double y) {
        Transform t = this.body.getTransform().copy();
        t.setTranslation(x, y);
        this.body.setTransform(t);
    }

    /**
     * Setzt Sichtbarkeit für Rendering.
     *
     * @param visible Sichtbarkeit
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return true, wenn sichtbar
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Prüft per UserData, ob ein ColliderBody ein Player ist.
     *
     * @param c ColliderBody
     * @return true, wenn Player
     */
    public static boolean isPlayer(ColliderBody c) {
        return c.getUserData().startsWith("ENTITY_PLAYER");
    }

    /**
     * Prüft per UserData, ob ein BodyFixture ein Player ist.
     *
     * @param c BodyFixture
     * @return true, wenn Player
     */
    public static boolean isPlayer(BodyFixture c) {
        return ((String) c.getUserData()).startsWith("ENTITY_PLAYER");
    }

    /**
     * @param c ColliderBodies
     * @return true, wenn mindestens einer ein Player ist
     */
    public static boolean containsPlayer(ColliderBody... c) {
        return Arrays.stream(c).anyMatch(_c -> _c.getUserData().startsWith("ENTITY_PLAYER"));
    }

    /**
     * @param c BodyFixtures
     * @return true, wenn mindestens einer ein Player ist
     */
    public static boolean containsPlayer(BodyFixture... c) {
        return Arrays.stream(c).anyMatch(_c -> ((String) _c.getUserData()).startsWith("ENTITY_PLAYER"));
    }

    /**
     * @param collider ColliderBody
     * @return true, wenn collider zu diesem Player gehört
     */
    public boolean is(ColliderBody collider) {
        return this.body.getUserData().equals(collider.getUserData());
    }

    /**
     * @param collider BodyFixture
     * @return true, wenn collider zu diesem Player gehört
     */
    public boolean is(BodyFixture collider) {
        return this.body.getUserData().equals(collider.getUserData());
    }

    /**
     * @param colliders ColliderBodies
     * @return true, wenn eines zu diesem Player gehört
     */
    public boolean is(ColliderBody... colliders) {
        return Arrays.stream(colliders).anyMatch(c -> this.body.getUserData().equals(c.getUserData()));
    }

    /**
     * @param colliders BodyFixtures
     * @return true, wenn eines zu diesem Player gehört
     */
    public boolean is(BodyFixture... colliders) {
        return Arrays.stream(colliders).anyMatch(c -> this.body.getUserData().equals(c.getUserData()));
    }

    /**
     * @return true, wenn Walk-Animation aktiv ist
     */
    private boolean isWalking() {
        return this.isCurrentAnimation(EntityState.WALKING);
    }

    /**
     * @param direction Richtung
     * @return true, wenn Walk-Animation in Richtung aktiv ist
     */
    private boolean isWalking(EntityDirection direction) {
        return this.isCurrentAnimation(direction, EntityState.WALKING);
    }

    /**
     * @return aktuelle Blickrichtung
     */
    public EntityDirection getDirection() {
        return this.direction;
    }

    /**
     * Registriert einen Listener für Richtungswechsel.
     *
     * @param onDirectionChange Callback
     */
    public void onDirectionChange(Consumer<EntityPlayer> onDirectionChange) {
        this.onDirectionChange.add(onDirectionChange);
    }

    /** Triggert alle registrierten Richtungswechsel-Listener. */
    private void onDirectionChange() {
        this.onDirectionChange.forEach(d -> d.accept(this));
    }

    /**
     * Reagiert auf A/D für Blickrichtung.
     * Bei Freeze werden Eingaben ggf. ignoriert.
     *
     * @param key KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent key) {
        if (this.freeze && !this.isWalking()) return;
        switch (key.getKeyCode()) {
            case KeyEvent.VK_A: {
                this.direction = EntityDirection.LEFT;
                this.onDirectionChange();
                break;
            }
            case KeyEvent.VK_D: {
                this.direction = EntityDirection.RIGHT;
                this.onDirectionChange();
                break;
            }
        }
    }

    /**
     * @return eindeutige Entity-ID
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * @return UserProfile des Spielers
     */
    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    /**
     * Vergleicht Player anhand der ID.
     *
     * @param o Vergleichsobjekt
     * @return true bei gleicher ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!(o instanceof Entity) && !(o instanceof EntityPlayer)) return false;
        EntityPlayer entity = (EntityPlayer) o;
        return Objects.equals(this.id, entity.getId());
    }
}
