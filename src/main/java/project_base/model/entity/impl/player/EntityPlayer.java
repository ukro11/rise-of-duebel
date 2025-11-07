package project_base.model.entity.impl.player;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import project_base.animation.AnimationRenderer;
import project_base.animation.states.entity.CharacterAnimationState;
import project_base.Wrapper;
import project_base.event.services.EventProcessCallback;
import project_base.event.services.process.EventLoadAssetsProcess;
import project_base.model.entity.Entity;
import project_base.physics.Collider;
import project_base.utils.Vec2;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.function.Consumer;

public class EntityPlayer extends Entity {

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
            if (this.freeze && this.isCurrentAnimation(false)) return;

            this.onMove();
            if (this.body.getVelocity().magnitude() == 0) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, false));
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
            this.renderer.switchState(this.getStateForEntityState(this.direction, true));
            verticalKeyDown = true;

        } else if (ViewController.isKeyDown(KeyEvent.VK_S) && !ViewController.isKeyDown(KeyEvent.VK_W)) {
            moveVelocity.set(null, this.speed);
            this.direction = EntityDirection.BOTTOM;
            this.renderer.switchState(this.getStateForEntityState(this.direction, true));
            verticalKeyDown = true;
        }

        if (ViewController.isKeyDown(KeyEvent.VK_A) && !ViewController.isKeyDown(KeyEvent.VK_D)) {
            moveVelocity.set(-this.speed, null);
            this.direction = EntityDirection.LEFT;
            if (!this.isCurrentAnimation(EntityDirection.TOP, true) && !this.isCurrentAnimation(EntityDirection.BOTTOM, true)) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, true));

            } else if (!verticalKeyDown) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, true));
            }

        } else if (ViewController.isKeyDown(KeyEvent.VK_D) && !ViewController.isKeyDown(KeyEvent.VK_A)) {
            moveVelocity.set(this.speed, null);
            this.direction = EntityDirection.RIGHT;
            if (!this.isCurrentAnimation(EntityDirection.TOP, true) && !this.isCurrentAnimation(EntityDirection.BOTTOM, true)) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, true));

            } else if (!verticalKeyDown) {
                this.renderer.switchState(this.getStateForEntityState(this.direction, true));
            }
        }
        if (moveVelocity.magnitude() > 0) {
            moveVelocity.normalize().mul(this.speed, this.speed);
        }
        this.body.setLinearVelocity(moveVelocity.x, moveVelocity.y);
    }

    private CharacterAnimationState getStateForEntityState(EntityDirection direction, boolean walking) {
        switch (direction) {
            case TOP: {
                if (walking) {
                    return !this.inventory.hasItemInventory() ? CharacterAnimationState.WALK_TOP : CharacterAnimationState.PLATE_WALK_TOP;

                } else {
                    return !this.inventory.hasItemInventory() ? CharacterAnimationState.IDLE_TOP : CharacterAnimationState.PLATE_IDLE_TOP;
                }
            }
            case LEFT: {
                if (walking) {
                    return !this.inventory.hasItemInventory() ? CharacterAnimationState.WALK_LEFT : CharacterAnimationState.PLATE_WALK_LEFT;

                } else {
                    return !this.inventory.hasItemInventory() ? CharacterAnimationState.IDLE_LEFT : CharacterAnimationState.PLATE_IDLE_LEFT;
                }
            }
            case BOTTOM: {
                if (walking) {
                    return !this.inventory.hasItemInventory() ? CharacterAnimationState.WALK_BOTTOM : CharacterAnimationState.PLATE_WALK_BOTTOM;

                } else {
                    return !this.inventory.hasItemInventory() ? CharacterAnimationState.IDLE_BOTTOM : CharacterAnimationState.PLATE_IDLE_BOTTOM;
                }
            }
            case RIGHT: {
                if (walking) {
                    return !this.inventory.hasItemInventory() ? CharacterAnimationState.WALK_RIGHT : CharacterAnimationState.PLATE_WALK_RIGHT;

                } else {
                    return !this.inventory.hasItemInventory() ? CharacterAnimationState.IDLE_RIGHT : CharacterAnimationState.PLATE_IDLE_RIGHT;
                }
            }
        }
        return null;
    }

    private boolean isCurrentAnimation(boolean walking) {
        return this.isCurrentAnimation(this.direction, walking);
    }

    private boolean isCurrentAnimation(EntityDirection direction, boolean walking) {
        switch (direction) {
            case TOP: {
                if (walking) {
                    return this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.WALK_TOP || this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.PLATE_WALK_TOP;

                } else {
                    return this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.IDLE_TOP || this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.PLATE_IDLE_TOP;
                }
            }
            case LEFT: {
                if (walking) {
                    return this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.WALK_LEFT || this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.PLATE_WALK_LEFT;

                } else {
                    return this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.IDLE_LEFT || this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.PLATE_IDLE_LEFT;
                }
            }
            case BOTTOM: {
                if (walking) {
                    return this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.WALK_BOTTOM || this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.PLATE_WALK_BOTTOM;

                } else {
                    return this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.IDLE_BOTTOM || this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.PLATE_IDLE_BOTTOM;
                }
            }
            case RIGHT: {
                if (walking) {
                    return this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.WALK_RIGHT || this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.PLATE_WALK_RIGHT;

                } else {
                    return this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.IDLE_RIGHT || this.renderer.getCurrentAnimation().getState() == CharacterAnimationState.PLATE_IDLE_RIGHT;
                }
            }
        }
        return false;
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
        if (this.freeze && this.isCurrentAnimation(false)) return;
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
