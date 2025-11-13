package rise_of_duebel.event.events.collider;

import rise_of_duebel.event.Event;
import rise_of_duebel.physics.Collider;

public class ColliderDestroyEvent extends Event {

    private Collider collider;

    public ColliderDestroyEvent(Collider collider) {
        super("collider_destroy");
        this.collider = collider;
    }

    public Collider getCollider() {
        return collider;
    }
}
