package duebel_level.graphics.camera;

import duebel_level.utils.MathUtils;
import org.dyn4j.geometry.Vector3;

import java.util.Random;

public class CameraShake implements CameraEffect {

    private double shakeDuration;

    // 0..1
    private double trauma;
    // wie schnell Shake verschwindet
    private double maxShakeOffset;
    private double maxShakeAngle;

    private double shakeOffsetX = 0.0;
    private double shakeOffsetY = 0.0;
    private double shakeAngle = 0.0;

    private double shakeTime = 0.0;

    // ChatGPT Shake Expertise
    private final Random shakeRandom = new Random();
    private final double nxPhase = shakeRandom.nextDouble() * Math.PI * 2;
    private final double nyPhase = shakeRandom.nextDouble() * Math.PI * 2;
    private final double naPhase = shakeRandom.nextDouble() * Math.PI * 2;

    private final double nxFreq = 9.0 + shakeRandom.nextDouble() * 6.0;  // 9..15
    private final double nyFreq = 9.0 + shakeRandom.nextDouble() * 6.0;
    private final double naFreq = 7.0 + shakeRandom.nextDouble() * 6.0;  // 7..13

    public CameraShake(ShakeType type) {
        this(type, type.getDuration());
    }

    public CameraShake(ShakeType type, double duration) {
        this.trauma = MathUtils.clamp(type.getTrauma(), 0.0, 1.0);
        this.shakeDuration = duration;

        this.maxShakeOffset = type.getMaxOffsetPx();
        this.maxShakeAngle = type.getMaxAngleDeg();
    }

    public CameraShake(double trauma, double duration) {
        this(trauma, duration, 4.0, 3.5);
    }

    public CameraShake(double trauma, double duration, double maxOffsetPx, double maxAngleDeg) {
        this.trauma = MathUtils.clamp(this.trauma + trauma, 0.0, 1.0);
        this.shakeDuration = duration;
        this.maxShakeOffset = maxOffsetPx;
        this.maxShakeAngle = maxAngleDeg;
    }

    private double smoothNoise(double t, double phase, double freq) {
        double a = Math.sin(t * freq + phase);
        double b = Math.sin(t * (freq * 0.5) + phase * 1.37) * 0.5;
        double c = Math.sin(t * (freq * 2.1) + phase * 0.73) * 0.25;
        return (a + b + c) / (1.0 + 0.5 + 0.25);
    }

    @Override
    public Vector3 initiate(CameraRenderer camera, double dt) {
        this.shakeDuration -= dt;
        if (this.shakeDuration <= 0.0) {
            this.trauma = 0.0;
            this.shakeOffsetX = 0.0;
            this.shakeOffsetY = 0.0;
            this.shakeAngle = 0.0;
            return new Vector3(0.0, 0.0, 0.0);
        }

        this.shakeTime += dt * 10;

        double s = this.trauma * this.trauma;

        double nX = smoothNoise(this.shakeTime, nxPhase, nxFreq);
        double nY = smoothNoise(this.shakeTime, nyPhase, nyFreq);
        double nA = smoothNoise(this.shakeTime, naPhase, naFreq);

        this.shakeOffsetX = nX * this.maxShakeOffset * s;
        this.shakeOffsetY = nY * this.maxShakeOffset * s;
        this.shakeAngle   = nA * this.maxShakeAngle * s;

        return new Vector3(this.shakeOffsetX, this.shakeOffsetY, this.shakeAngle);
    }

    @Override
    public boolean isFinished() {
        return this.shakeDuration <= 0.0;
    }

    public enum ShakeType {
        SMALL_HIT(1.0, 0.1, 3.0, 0),
        EXPLOSION(0.6, 0.22, 34.0, 9.0);

        // --- Core shake ---
        private final double trauma;
        private final double duration;
        private final double maxOffsetPx;
        private final double maxAngleDeg;

        ShakeType(
            double trauma,
            double duration,
            double maxOffsetPx,
            double maxAngleDeg
        ) {
            this.trauma = trauma;
            this.duration = duration;
            this.maxOffsetPx = maxOffsetPx;
            this.maxAngleDeg = maxAngleDeg;
        }

        public double getTrauma() { return trauma; }
        public double getDuration() { return duration; }
        public double getMaxOffsetPx() { return maxOffsetPx; }
        public double getMaxAngleDeg() { return maxAngleDeg; }
    }
}
