package rise_of_duebel.event;

public interface EventListener<T extends Event> {
    void handle(T event);
}
