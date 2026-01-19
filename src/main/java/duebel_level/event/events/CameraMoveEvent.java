package duebel_level.event.events;

import duebel_level.event.Event;
import duebel_level.graphics.camera.CameraRenderer;

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