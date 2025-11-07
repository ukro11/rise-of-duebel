package project_base.animation.states.spawner;

import com.google.common.collect.Range;
import project_base.animation.IAnimationState;

public enum TablePanAnimationState implements IAnimationState {
    DEFAULT(0, Range.closed(0, 0), 1, 0.1),
    FOCUS(1, Range.closed(0, 0), 1, 0.1),
    COOK(2, Range.closed(0, 0), 3, 1.0);


    private final int rowIndex;
    private final Range<Integer> columnRange;
    private final int frames;
    private final double duration;
    private final boolean loop;
    private final boolean reverse;
    private final int frameWidth;
    private final int frameHeight;

    TablePanAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
        this(rowIndex, columnRange, frames, duration, true);
    }

    TablePanAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
        this(rowIndex, columnRange, frames, duration, loop, false);
    }

    TablePanAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
        this(rowIndex, columnRange, frames, duration, loop, reverse, 0, 0);
    }

    TablePanAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse, int frameWidth, int frameHeight) {
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
        return getClass().getTypeName() + " {" +
                "\n   rowIndex=" + this.rowIndex +
                "\n   , columnRange=" + this.columnRange +
                "\n   , frames=" + this.frames +
                "\n   , duration=" + this.duration +
                "\n   , loop=" + this.loop +
                "\n   , reverse=" + this.reverse +
                "\n   , frameWidth=" + this.frameWidth +
                "\n   , frameHeight=" + this.frameHeight +
                '}';
    }
}