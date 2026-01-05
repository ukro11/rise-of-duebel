package rise_of_duebel.event.events;

import rise_of_duebel.event.Event;
import rise_of_duebel.graphics.camera.CameraRenderer;

public class CameraMoveEvent extends Event {

    private CameraRenderer camera;

    public CameraMoveEvent(CameraRenderer camera) {
        super("cameraMove");
        this.camera = camera;
    }

    public CameraRenderer getCamera() {
        return this.camera;
    }
}