package rise_of_duebel.event.services.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.event.services.EventProcess;
import rise_of_duebel.event.services.EventProcessCallback;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EventHttpProcess extends EventProcess {

    private Logger logger = LoggerFactory.getLogger(EventHttpProcess.class);
    private String url;
    private EventProcessCallback<EventHttpProcessData> callback;
    private final HttpClient client;
    private final HttpRequest request;

    public EventHttpProcess(String name, String url) {
        this(name, url, (EventProcessCallback<EventHttpProcessData>) null);
    }

    public EventHttpProcess(String name, String url, EventProcessCallback<EventHttpProcessData> callback) {
        super(name);
        this.url = url;
        this.callback = callback;

        this.client = HttpClient.newHttpClient();
        this.request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    }

    public EventHttpProcess(String name, String url, HttpRequest.Builder builder) {
        this(name, url, builder, null);
    }

    public EventHttpProcess(String name, String url, HttpRequest.Builder builder, EventProcessCallback<EventHttpProcessData> callback) {
        super(name);
        this.url = url;
        this.callback = callback;

        this.client = HttpClient.newHttpClient();
        this.request = builder.uri(URI.create(url)).build();
    }

    @Override
    public void run() {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.logger.info("Sending HTTP Request {}", this.url);
            if (this.callback != null) this.callback.onSuccess(new EventHttpProcessData(this.request, response));

        } catch (InterruptedException | IOException e) {
            this.logger.info("Error while sending HTTP Request", e.getMessage());
            if (this.callback != null) this.callback.onFailure(e);
        }
    }

    public class EventHttpProcessData {
        private final HttpRequest request;
        private final HttpResponse<String> response;

        public EventHttpProcessData(HttpRequest request, HttpResponse<String> response) {
            this.request = request;
            this.response = response;
        }

        public HttpRequest getRequest() {
            return this.request;
        }

        public HttpResponse<String> getResponse() {
            return this.response;
        }

        @Override
        public String toString() {
            return "EventHttpProcessData{" +
                    "request=" + request +
                    ", response=" + response +
                    '}';
        }
    }
}
