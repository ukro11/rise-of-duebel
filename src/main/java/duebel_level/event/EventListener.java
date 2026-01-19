package duebel_level.event;

public interface EventListener<T extends Event> {
    void handle(T event);
}
