package rise_of_duebel.model.entity.impl.player;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import rise_of_duebel.Wrapper;
import rise_of_duebel.animation.AnimationRenderer;
import rise_of_duebel.animation.entity.EntityState;
import rise_of_duebel.animation.states.CharacterAnimationState;
import rise_of_duebel.event.services.EventProcessCallback;
import rise_of_duebel.event.services.process.EventLoadAssetsProcess;
import rise_of_duebel.model.entity.Entity;
import rise_of_duebel.model.entity.EntityDirection;
import rise_of_duebel.physics.Collider;
import rise_of_duebel.utils.Vec2;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class EntityPlayer extends Entity<CharacterAnimationState> {

    private EntityDirection direction = EntityDirection.BOTTOM;
    private PlayerInventory inventory;

    private final double speed = 140.0;
    private boolean freeze = false;

    private List<Consumer<EntityPlayer>> onDirectionChange = new ArrayList<>();

    public EntityPlayer(Collider collider, double x, double y, double width, double height) {
        super(collider, x, y, width, height);
        this.exitOnWrongRegistration();

        Wrapper.getProcessManager().queue(new EventLoadAssetsProcess<AnimationRenderer>("Loading animations", () -> new AnimationRenderer(
                "/graphic/character/ali.png", 5, 24, 16, 32,
                CharacterAnimationState.IDLE_BOTTOM
        ), new EventProcessCallback<AnimationRenderer>() {
            @Override
            public void onSuccess(AnimationRenderer data) {
                setRenderer(data);
            }
        }));
        this.inventory = new PlayerInventory(this);
        this.highestPointOffset = new Vec2(this.width / 2, this.height / 2);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (!this.body.isDestroyed() && this.renderer != null) {
            // EntityPlayer.IDLE_STATES.contains(this.renderer.getCurrentAnimation().getState())
            if (this.freeze && !this.isWalking()) return;

            this.onMove();
            if (this.body.getVelocity().magnitude() == 0) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.IDLE));
            }
        }
    }

    @Override
    public double zIndex() {
        return super.zIndex() + 5;
    }

    @Override
    protected void drawEntity(DrawTool drawTool) {
        if (this.renderer != null && this.renderer.getCurrentFrame() != null) {
            drawTool.getGraphics2D().drawImage(
                this.renderer.getCurrentFrame(),
                this.inventory.hasItemInventory() ? (int) this.getX() - this.renderer.getCurrentFrame().getWidth() / 2 : (int) this.getX(),
                (int) this.getY(),
                this.inventory.hasItemInventory() ? (int) this.height : (int) this.width,
                (int) this.height,
                null
            );
        }
    }

    private void onMove() {
        if (!this.viewController.getDrawFrame().isFocused()) {
            // ONLY LOCAL PLAYER
            this.body.setLinearVelocity(0, 0);
            return;
        }
        if (this.freeze) return;

        boolean verticalKeyDown = false;
        Vec2 moveVelocity = new Vec2();
        if (ViewController.isKeyDown(KeyEvent.VK_W) && !ViewController.isKeyDown(KeyEvent.VK_S)) {
            moveVelocity.set(null, -this.speed);
            this.direction = EntityDirection.TOP;
            this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.WALKING));
            verticalKeyDown = true;

        } else if (ViewController.isKeyDown(KeyEvent.VK_S) && !ViewController.isKeyDown(KeyEvent.VK_W)) {
            moveVelocity.set(null, this.speed);
            this.direction = EntityDirection.BOTTOM;
            this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.WALKING));
            verticalKeyDown = true;
        }

        if (ViewController.isKeyDown(KeyEvent.VK_A) && !ViewController.isKeyDown(KeyEvent.VK_D)) {
            moveVelocity.set(-this.speed, null);
            this.direction = EntityDirection.LEFT;
            if (!this.isWalking(EntityDirection.TOP) && !this.isWalking(EntityDirection.BOTTOM)) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.WALKING));

            } else if (!verticalKeyDown) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.WALKING));
            }

        } else if (ViewController.isKeyDown(KeyEvent.VK_D) && !ViewController.isKeyDown(KeyEvent.VK_A)) {
            moveVelocity.set(this.speed, null);
            this.direction = EntityDirection.RIGHT;
            if (!this.isWalking(EntityDirection.TOP) && !this.isWalking(EntityDirection.BOTTOM)) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.WALKING));

            } else if (!verticalKeyDown) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, EntityState.WALKING));
            }
        }
        if (moveVelocity.magnitude() > 0) {
            moveVelocity.normalize().mul(this.speed, this.speed);
        }
        this.body.setLinearVelocity(moveVelocity.x, moveVelocity.y);
    }

    private List<CharacterAnimationState> getStatesForEntityState(EntityDirection direction, EntityState state) {
        List<CharacterAnimationState> s = List.of();
        for (CharacterAnimationState anim : CharacterAnimationState.values()) {
            if (anim.getDirection() == direction && anim.getState() == state) {
                s.add(anim);
            }
        }
        return s;
    }

    private CharacterAnimationState getStateForEntityState(EntityDirection direction, EntityState state) {
        for (CharacterAnimationState anim : CharacterAnimationState.values()) {
            if (anim.getDirection() == direction && anim.getState() == state) {
                return anim;
            }
        }
        return null;
    }

    private boolean isCurrentAnimation(EntityState state) {
        var anim = this.renderer.getCurrentAnimation().getState();
        return anim.getState() == state;
    }

    private boolean isCurrentAnimation(EntityDirection direction, EntityState state) {
        var anim = this.renderer.getCurrentAnimation().getState();
        return anim.getDirection() == direction && anim.getState() == state;
    }

    private boolean isWalking() {
        return this.isCurrentAnimation(EntityState.WALKING);
    }

    private boolean isWalking(EntityDirection direction) {
        return this.isCurrentAnimation(direction, EntityState.WALKING);
    }

    public void freeze(boolean flag) {
        this.freeze = flag;
    }

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public EntityDirection getDirection() {
        return this.direction;
    }

    public void onDirectionChange(Consumer<EntityPlayer> onDirectionChange) {
        this.onDirectionChange.add(onDirectionChange);
    }

    private void onDirectionChange() {
        this.onDirectionChange.forEach(d -> d.accept(this));
    }

    @Override
    public void keyPressed(KeyEvent key) {
        // EntityPlayer.IDLE_STATES.contains(this.renderer.getCurrentAnimation().getState())
        if (this.freeze && !this.isWalking()) return;
        switch (key.getKeyCode()) {
            case KeyEvent.VK_W: {
                this.direction = EntityDirection.TOP;
                this.onDirectionChange();
                break;
            }
            case KeyEvent.VK_A: {
                this.direction = EntityDirection.LEFT;
                this.onDirectionChange();
                break;
            }
            case KeyEvent.VK_S: {
                this.direction = EntityDirection.BOTTOM;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!(o instanceof Entity) && !(o instanceof EntityPlayer)) return false;
        EntityPlayer entity = (EntityPlayer) o;
        return Objects.equals(this.id, entity.getId());
    }
}
