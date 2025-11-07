package project_base.event.events.collider;

import project_base.event.Event;
import project_base.physics.Collider;

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
