package duebel_level.utils;

public class TimerUtils {

    private double runningTime = 0;
    private double elapsedTime = 0;
    private double lastTime = System.nanoTime() / 1e9;
    private double deltaTime = 0.0;
    private int fps = 0;
    private int frameCount = 0;
    public int TARGET_FPS = 240;
    private double FPS_UPDATE_INTERVAL = 1.0;
    private double FRAME_TIME = 1000 / this.TARGET_FPS;
    private boolean updated = false;

    public void update() {
        double currentTime = System.nanoTime() / 1e9;
        this.deltaTime = currentTime - this.lastTime;

        this.lastTime = currentTime;

        if (this.deltaTime <= 0) {
            this.deltaTime = 0.000_001;
        }

        this.runningTime += this.deltaTime;
        this.elapsedTime += this.deltaTime;

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(boolean sleep) {
        double currentTime = System.nanoTime() / 1e9;
        this.deltaTime = (currentTime - this.lastTime);

        this.lastTime = currentTime;

        if (this.deltaTime == 0) this.deltaTime = 0.01;

        this.elapsedTime += this.deltaTime;
        this.runningTime += this.deltaTime;
        if (this.elapsedTime >= this.FPS_UPDATE_INTERVAL) {
            this.updated = true;
        }

        if (sleep) {
            long sleepTime = Math.max(0, (long) (this.FRAME_TIME - this.deltaTime * 1000));
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateFrames() {
        this.frameCount++;

        if (this.elapsedTime >= this.FPS_UPDATE_INTERVAL) {
            this.fps = this.frameCount;
            this.frameCount = 0;
            this.elapsedTime = 0;
        }
    }

    public double getRunningTime() {
        return this.runningTime;
    }

    public int getFPSCap() {
        return this.TARGET_FPS;
    }

    public double getDeltaTime() {
        return this.deltaTime;
    }

    public int getFPS() {
        return this.fps;
    }

    public boolean fpsUpdated() {
        return this.updated;
    }
}

