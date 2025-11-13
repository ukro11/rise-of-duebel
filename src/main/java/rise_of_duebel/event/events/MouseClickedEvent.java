package rise_of_duebel.event.events;

import rise_of_duebel.event.Event;

import java.awt.event.MouseEvent;

public class MouseClickedEvent extends Event {

    private MouseEvent event;

    public MouseClickedEvent(MouseEvent event) {
        super("mouseclicked");
        this.event = event;
    }

    public MouseEvent getMouseEvent() {
        return event;
    }

    public int getButton() {
        return this.event.getButton();
    }

    public int getX() {
        return this.event.getX();
    }

    public int getY() {
        return this.event.getY();
    }

    @Override
    public String toString() {
        return "MouseClickedEvent{" +
                "button=" + this.getButton() +
                ", x=" + this.getX() +
                ", y=" + this.getY() +
                '}';
    }
}
