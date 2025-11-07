package project_base.event;

import project_base.Wrapper;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private final Map<String, CopyOnWriteArrayList<EventListener>> listeners = new HashMap<>();

    /**
     * Die Methode sorgt dafür, dass man eigene Funktionen bei Events aufrufen kann. <br> <br>
     * <p>
     *     Beispiel für <strong>UpdateEvent</strong>:
     *     <pre>
     *         {@code Wrapper.getEventManager().addEventListener("update", (UpdateEvent e) -> ProgramController.logger.info("{}", e.getDeltaTime()));}
     *     </pre>
     * </p>
     *
     * @param event Das Event, das aufgerufen werden soll
     * @param listener Die Funktion, die aufgerufen werden soll, wenn das Event aufgerufen wird
     */
    public void addEventListener(String event, EventListener<? extends Event> listener) {
        this.listeners.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Die Methode sorgt dafür, dass man eigene Funktionen bei Events aufrufen kann.
     * Ein Unterschied zu {@code addEventListener()} ist, dass die angegebene Funktion nur
     * einmal ausgeführt wird und danach entfernt wird. <br> <br>
     * <p>
     *     Beispiel für <strong>UpdateEvent</strong>:
     *     <pre>
     *          {@code Wrapper.getEventManager().once("update", (UpdateEvent e) -> ProgramController.logger.info("{}", e.getDeltaTime()))}
     *     </pre>
     * </p>
     *
     * @param event Das Event, das aufgerufen werden soll
     * @param listener Die Funktion, die aufgerufen werden soll, wenn das Event aufgerufen wird
     */
    public <T extends Event> void once(String event, EventListener<T> listener) {
        EventListener<T> wrapper = new EventListener<T>() {
            @Override
            public void handle(T data) {
                listener.handle(data);
                Wrapper.getEventManager().removeEventListener(event, this);
            }
        };
        this.addEventListener(event, wrapper);
    }

    /**
     * Die Methode sorgt dafür, dass eine bestimmte Funktion, die zu {@code event}
     * zugewiesen ist, entfernt wird.
     *
     * @param event Das Event, das die gesuchte Funktion zugewiesen worden ist
     * @param listener Die Funktion, die gelöscht werden soll
     */
    public void removeEventListener(String event, EventListener listener) {
        List<EventListener> eventListeners = this.listeners.get(event);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * Die Methode sorgt dafür, dass man alle Funktionen, die zu {@code event} zugewiesen sind,
     * entfernt werden und nicht mehr aufgerufen werden
     *
     * @param event Das Event, was alle Funktionen entfernen soll
     */
    public void removeAllEventListener(String event) {
        this.listeners.remove(event);
    }

    /**
     * Die Methode sorgt dafür, dass das {@code event} aufgerufen wird, und dass die Funktionen
     * von den EventListener getriggert werden.
     *
     * @param event Das Event, das getriggert werden soll
     */
    public void dispatchEvent(Event event) {
        List<EventListener> eventListeners = this.listeners.get(event.getType());
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.handle(event);
            }
        }
    }
}
