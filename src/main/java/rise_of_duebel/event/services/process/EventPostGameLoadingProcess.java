package rise_of_duebel.event.services.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.event.services.EventProcess;
import rise_of_duebel.event.services.EventProcessCallback;

import java.util.function.Supplier;

public class EventPostGameLoadingProcess<T> extends EventProcess {

    private Logger logger = LoggerFactory.getLogger(EventPostGameLoadingProcess.class);
    private Object func;
    private EventProcessCallback<T> callback;

    public EventPostGameLoadingProcess(String name, Supplier<T> func) {
        this(name, func, null);
    }

    public EventPostGameLoadingProcess(String name, Supplier<T> func, EventProcessCallback<T> callback) {
        super(name);
        this.func = func;
        this.callback = callback;
    }

    public EventPostGameLoadingProcess(String name, Runnable func) {
        this(name, func, null);
    }

    public EventPostGameLoadingProcess(String name, Runnable func, EventProcessCallback<T> callback) {
        super(name);
        this.func = func;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            if (this.func instanceof Supplier) {
                T data = ((Supplier<T>) this.func).get();
                if (this.callback != null) this.callback.onSuccess(data);

            } else if (this.func instanceof Runnable) {
                ((Runnable) this.func).run();
                if (this.callback != null) this.callback.onSuccess(null);

            } else {
                this.logger.error("The event process {} couldnt be processed because \"func\" is not a Runnable or Supplier!", this.getName());
                this.callback.onSuccess(null);
            }

        } catch (Exception e) {
            this.logger.error("Error while processing assets", e);
            if (this.callback != null) this.callback.onFailure(e);
        }
    }
}
