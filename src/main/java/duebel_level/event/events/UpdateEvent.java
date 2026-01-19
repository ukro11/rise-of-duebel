package duebel_level.event.events;

import duebel_level.event.Event;

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
