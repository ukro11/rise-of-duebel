package rise_of_duebel.animation.states;

import com.google.common.collect.Range;
import rise_of_duebel.animation.IAnimationState;

public enum FocusAnimationState implements IAnimationState {
    DEFAULT(0, Range.closed(0, 0), 1, 0.1, true),
    FOCUS(1, Range.closed(0, 0), 1, 0.1, true);

    private final int rowIndex;
    private final Range<Integer> columnRange;
    private final int frames;
    private final double duration;
    private final boolean loop;
    private final boolean reverse;
    private final int frameWidth;
    private final int frameHeight;

    FocusAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
        this(rowIndex, columnRange, frames, duration, true);
    }

    FocusAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
        this(rowIndex, columnRange, frames, duration, loop, false);
    }

    FocusAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
        this(rowIndex, columnRange, frames, duration, loop, reverse, 0, 0);
    }

    FocusAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse, int frameWidth, int frameHeight) {
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

