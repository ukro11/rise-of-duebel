package duebel_level.event.events;

import duebel_level.event.Event;

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
