package project_base.event;

public interface EventListener<T extends Event> {
    void handle(T event);
}
