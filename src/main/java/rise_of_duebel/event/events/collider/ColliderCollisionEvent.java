package rise_of_duebel.event.events.collider;

import rise_of_duebel.event.Event;
import rise_of_duebel.physics.Collider;

public class ColliderCollisionEvent extends Event {

    private final Collider firstCollider;
    private final Collider secondCollider;
    private final CollisionState state;

    public ColliderCollisionEvent(Collider firstCollider, Collider secondCollider, CollisionState state) {
        super("collider_collision");
        this.firstCollider = firstCollider;
        this.secondCollider = secondCollider;
        this.state = state;
    }

    public Collider getFirstCollider() {
        return firstCollider;
    }

    public Collider getSecondCollider() {
        return secondCollider;
    }

    public CollisionState getState() {
        return this.state;
    }

    /**
     * Die Methode überprüft, ob {@code body} dasselbe ist wie {@code mainBody} oder {@code collidedBody}.
     * @return {@code true}, wenn {@code body} {@code mainBody} oder {@code collidedBody} ist.
     */
    public boolean isBodyInvolved(Collider body) {
        return this.firstCollider.equals(body) || this.secondCollider.equals(body);
    }

    @Override
    public String toString() {
        return "CollisionEvent{" +
                "firstBody=" + this.firstCollider.getId() +
                ", secondBody=" + this.secondCollider.getId() +
                ", state=" + state +
                '}';
    }

    public enum CollisionState {
        COLLISION_BEGIN_CONTACT,
        COLLISION_NORMAL_CONTACT,
        COLLISION_END_CONTACT
    }
}
