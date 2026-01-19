package duebel_level.event.events.collider;

import duebel_level.dyn4j.ColliderBody;
import duebel_level.event.Event;

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
