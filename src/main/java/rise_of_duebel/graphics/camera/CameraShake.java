package rise_of_duebel.graphics.camera;

import org.dyn4j.geometry.Vector3;
import rise_of_duebel.utils.MathUtils;

import java.util.Random;

public class CameraShake implements CameraEffect {

    private double shakeDuration;

    // 0..1
    private double trauma;
    // wie schnell Shake verschwindet
    private double traumaDecayPerSecond;
    private double maxShakeOffset;
    private double maxShakeAngle;

    private double shakeOffsetX = 0.0;
    private double shakeOffsetY = 0.0;
    private double shakeAngle = 0.0;

    private double shakeTime = 0.0;

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
        this.traumaDecayPerSecond = type.getDecayPerSecond();
    }

    public CameraShake(double trauma, double duration) {
        this(trauma, duration, 4.0, 3.5, 1.5);
    }

    public CameraShake(double trauma, double duration, double maxOffsetPx, double maxAngleDeg, double decayPerSecond) {
        this.trauma = MathUtils.clamp(this.trauma + trauma, 0.0, 1.0);
        this.shakeDuration = duration;
        this.maxShakeOffset = maxOffsetPx;
        this.maxShakeAngle = maxAngleDeg;
        this.traumaDecayPerSecond = decayPerSecond;
    }

    private double smoothNoise(double t, double phase, double freq) {
        double a = Math.sin(t * freq + phase);
        double b = Math.sin(t * (freq * 0.5) + phase * 1.37) * 0.5;
        double c = Math.sin(t * (freq * 2.1) + phase * 0.73) * 0.25;
        return (a + b + c) / (1.0 + 0.5 + 0.25);
    }

    @Override
    public Vector3 initiate(CameraRenderer camera, double dt) {
        if (this.shakeDuration > 0.0) {
            this.shakeDuration -= dt;

        } else {
            if (this.trauma > 0.0) {
                this.trauma = Math.max(0.0,
                        this.trauma - this.traumaDecayPerSecond * dt);
            }
        }

        double s = this.trauma * this.trauma;
        this.shakeTime += dt;

        double nX = smoothNoise(this.shakeTime, nxPhase, nxFreq);
        double nY = smoothNoise(this.shakeTime, nyPhase, nyFreq);
        double nA = smoothNoise(this.shakeTime, naPhase, naFreq);

        this.shakeOffsetX = nX * this.maxShakeOffset * s;
        this.shakeOffsetY = nY * this.maxShakeOffset * s;
        this.shakeAngle   = nA * this.maxShakeAngle * s;

        if (s < 0.0001) {
            this.shakeOffsetX = 0.0;
            this.shakeOffsetY = 0.0;
            this.shakeAngle = 0.0;
        }
        return new Vector3(this.shakeOffsetX, this.shakeOffsetY, this.shakeAngle);
    }

    @Override
    public boolean isFinished() {
        return this.shakeOffsetX == 0 && this.shakeOffsetY == 0 && this.shakeAngle == 0;
    }

    public enum ShakeType {
        SMALL_HIT(1.0, 0.1, 3.0, 0, 10.5),
        EXPLOSION(0.6, 0.22, 34.0, 9.0, 4.5);

        // --- Core shake ---
        private final double trauma;
        private final double duration;
        private final double maxOffsetPx;
        private final double maxAngleDeg;
        private final double decayPerSecond;

        ShakeType(
            double trauma,
            double duration,
            double maxOffsetPx,
            double maxAngleDeg,
            double decayPerSecond
        ) {
            this.trauma = trauma;
            this.duration = duration;
            this.maxOffsetPx = maxOffsetPx;
            this.maxAngleDeg = maxAngleDeg;
            this.decayPerSecond = decayPerSecond;
        }

        public double getTrauma() { return trauma; }
        public double getDuration() { return duration; }
        public double getMaxOffsetPx() { return maxOffsetPx; }
        public double getMaxAngleDeg() { return maxAngleDeg; }
        public double getDecayPerSecond() { return decayPerSecond; }
    }
}
