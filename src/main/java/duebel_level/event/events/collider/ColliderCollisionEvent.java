package duebel_level.event.events.collider;

import duebel_level.event.Event;
import org.dyn4j.dynamics.contact.Contact;

public class ColliderCollisionEvent extends Event {

    private final Contact contact;

    public ColliderCollisionEvent(Contact contact) {
        super("collider_collision");
        this.contact = contact;
    }

    public Contact getContact() {
        return this.contact;
    }

    @Override
    public String toString() {
        return "CollisionEvent{" +
                "contact=" + this.contact.toString() +
                '}';
    }
}
