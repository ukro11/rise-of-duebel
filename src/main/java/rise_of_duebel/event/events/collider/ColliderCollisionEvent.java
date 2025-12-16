package rise_of_duebel.event.events.collider;

import org.dyn4j.dynamics.contact.Contact;
import rise_of_duebel.event.Event;

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
