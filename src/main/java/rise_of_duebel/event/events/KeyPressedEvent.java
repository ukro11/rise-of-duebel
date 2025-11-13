package rise_of_duebel.event.events;

import rise_of_duebel.event.Event;

import java.awt.event.KeyEvent;

public class KeyPressedEvent extends Event {

    private final KeyEvent keyEvent;

    public KeyPressedEvent(KeyEvent keyEvent) {
        super("keypressed");
        this.keyEvent = keyEvent;
    }

    public int getKeyCode() {
        return this.keyEvent.getKeyCode();
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }
}
