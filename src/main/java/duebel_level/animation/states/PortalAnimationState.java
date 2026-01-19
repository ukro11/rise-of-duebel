package duebel_level.animation.states;

import com.google.common.collect.Range;
import duebel_level.animation.IAnimationState;

public enum PortalAnimationState implements IAnimationState {
    IDLE(0, Range.closed(0, 7), 8, 1.1, true),
    DISAPPEAR(1, Range.closed(0, 11), 12, 0.6, false);

    private final int rowIndex;
    private final Range<Integer> columnRange;
    private final int frames;
    private final double duration;
    private final boolean loop;
    private final boolean reverse;
    private final int frameWidth;
    private final int frameHeight;

    PortalAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
        this(rowIndex, columnRange, frames, duration, true);
    }

    PortalAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
        this(rowIndex, columnRange, frames, duration, loop, false);
    }

    PortalAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
        this(rowIndex, columnRange, frames, duration, loop, reverse, 0, 0);
    }

    PortalAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse, int frameWidth, int frameHeight) {
        this.rowIndex = rowIndex;
        this.columnRange = columnRange;
        this.frames = frames;
        this.duration = duration;
        this.loop = loop;
        this.reverse = reverse;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    @Override
    public Range<Integer> getColumnRange() {
        return this.columnRange;
    }

    @Override
    public int getFrames() {
        return this.frames;
    }

    @Override
    public double getDuration() {
        return this.duration;
    }

    @Override
    public boolean isLoop() {
        return this.loop;
    }

    @Override
    public boolean isReverse() {
        return this.reverse;
    }

    @Override
    public int getFrameWidth() {
        return this.frameWidth;
    }

    @Override
    public int getFrameHeight() {
        return this.frameHeight;
    }

    @Override
    public String toString() {
        return this.name();
    }
}


