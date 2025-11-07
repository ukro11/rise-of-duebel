package project_base.event.events;

import project_base.event.Event;

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
