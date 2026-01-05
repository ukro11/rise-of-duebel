package rise_of_duebel.graphics.camera;

import org.dyn4j.geometry.Vector3;

public interface CameraEffect {
    Vector3 initiate(CameraRenderer camera, double dt);
    boolean isFinished();
}
