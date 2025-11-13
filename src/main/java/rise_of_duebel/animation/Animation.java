package rise_of_duebel.animation;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Animation<T extends Enum<T> & IAnimationState> {

    private final T state;
    private final CopyOnWriteArrayList<BufferedImage> frames;
    private final double duration;
    private final boolean loop;
    private final boolean reverse;
    private final double durationPerFrame;

    public Animation(T state, List<BufferedImage> frames, double duration, boolean loop, boolean reverse) {
        this.state = state;
        this.frames = new CopyOnWriteArrayList<>(frames);
        this.duration = duration;
        this.loop = loop;
        this.reverse = reverse;
        this.durationPerFrame = duration / frames.size();
    }

    public T getState() {
        return this.state;
    }

    public CopyOnWriteArrayList<BufferedImage> getFrames() {
        return this.frames;
    }

    public double getDuration() {
        return this.duration;
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public boolean isLoop() {
        return this.loop;
    }

    public double getDurationPerFrame() {
        return this.durationPerFrame;
    }

    @Override
    public String toString() {
        return "Animation{" +
                "state=" + state +
                ", duration=" + duration +
                ", loop=" + loop +
                ", reverse=" + reverse +
                ", durationPerFrame=" + durationPerFrame +
                ", frames=" + frames.size() +
                '}';
    }
}
