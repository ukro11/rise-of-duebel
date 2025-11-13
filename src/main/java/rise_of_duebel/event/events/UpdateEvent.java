package rise_of_duebel.event.events;

import rise_of_duebel.event.Event;

public class UpdateEvent extends Event {

    private double deltaTime;

    public UpdateEvent(double dt) {
        super("update");
        this.deltaTime = dt;
    }

    public double getDeltaTime() {
        return deltaTime;
    }
}
