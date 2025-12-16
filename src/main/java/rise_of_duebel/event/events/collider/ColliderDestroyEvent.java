package rise_of_duebel.event.events.collider;

import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.event.Event;

public class ColliderDestroyEvent extends Event {

    private ColliderBody collider;

    public ColliderDestroyEvent(ColliderBody collider) {
        super("collider_destroy");
        this.collider = collider;
    }

    public ColliderBody getCollider() {
        return collider;
    }
}
