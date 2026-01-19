package duebel_level.event.services.process;

import duebel_level.event.services.EventProcess;
import duebel_level.event.services.EventProcessCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventDownloadProcess extends EventProcess {

    private Logger logger = LoggerFactory.getLogger(EventDownloadProcess.class);
    private String url;
    private EventDownloadOutput output;
    private EventDownloadProcessCallback<EventDownloadProcessData> callback;

    public EventDownloadProcess(String name, String url, EventDownloadOutput output) {
        this(name, url, output, null);
    }

    public EventDownloadProcess(String name, String url, EventDownloadOutput output, EventDownloadProcessCallback<EventDownloadProcessData> callback) {
        super(name);
        this.url = url;
        this.output = output;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            URL urlObj = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            int totalSize = connection.getContentLength();
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(this.output.getOutputFile())) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                int totalRead = 0;
                int lastProgress = 0;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    totalRead += bytesRead;
                    int progress = (int) ((totalRead / (float) totalSize) * 100);
                    if (this.callback != null) this.callback.onProgress(new EventDownloadProcessData(this.output, progress, fileOutputStream, dataBuffer));
                }
                this.logger.info("Download {} is finished.", this.url);
                if (this.callback != null) this.callback.onSuccess(new EventDownloadProcessData(this.output, 100, fileOutputStream, dataBuffer));
            }
        } catch (IOException e) {
            this.logger.info("Error while downloading file:", e.getMessage());
            this.callback.onFailure(e);
        }
    }

    public class EventDownloadProcessData {
        private final EventDownloadOutput output;
        private final int progress;
        private final FileOutputStream stream;
        private final byte[] dataBuffer;

        public EventDownloadProcessData(EventDownloadOutput output, int progress, FileOutputStream stream, byte[] dataBuffer) {
            this.output = output;
            this.progress = progress;
            this.stream = stream;
            this.dataBuffer = dataBuffer;
        }

        public EventDownloadOutput getOutput() {
            return this.output;
        }

        public int getProgress() {
            return this.progress;
        }

        public FileOutputStream getStream() {
            return stream;
        }

        public byte[] getDataBuffer() {
            return dataBuffer;
        }

        @Override
        public String toString() {
            return "EventDownloadProcessData{" +
                    "output=" + this.output +
                    ", progress=" + this.progress +
                    '}';
        }
    }

    public interface EventDownloadProcessCallback<T> extends EventProcessCallback<T> {
        void onProgress(T data);
    }

    public static class EventDownloadOutput {

        private final String outputPath;
        private final File outputFile;
        private boolean resourceFolder = true;

        public EventDownloadOutput(String path, String outputFile) {
            this.outputPath = path;
            this.outputFile = new File(getClass().getResource(this.outputPath).getFile(), outputFile);
        }

        public EventDownloadOutput(String path, String outputFile, boolean resourceFolder) {
            this.outputPath = path;
            this.resourceFolder = resourceFolder;
            if (this.resourceFolder) {
                this.outputFile = new File(getClass().getResource(this.outputPath).getFile(), outputFile);

            } else {
                this.outputFile = new File(this.outputPath, outputFile);
            }
        }

        public String getOutputPath() {
            return outputPath;
        }

        public File getOutputFile() {
            return outputFile;
        }

        public boolean isResourceFolder() {
            return resourceFolder;
        }

        @Override
        public String toString() {
            return "EventDownloadOutput{" +
                    "outputPath='" + outputPath + '\'' +
                    ", outputFile=" + outputFile +
                    ", resourceFolder=" + resourceFolder +
                    '}';
        }
    }
}
