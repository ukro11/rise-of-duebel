package rise_of_duebel.event;

public class Event {
    private final String type;

    public Event(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
