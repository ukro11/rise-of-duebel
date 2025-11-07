package project_base.animation.states.entity;

import com.google.common.collect.Range;
import project_base.animation.IAnimationState;

public enum CharacterAnimationState implements IAnimationState {
    STILL_TOP(0, Range.closed(1, 1), 1, 0.5),
    STILL_BOTTOM(0, Range.closed(3, 3), 1, 0.5),
    STILL_LEFT(0, Range.closed(2, 2), 1, 0.5),
    STILL_RIGHT(0, Range.closed(0, 0), 1, 0.5),

    IDLE_TOP(1, Range.closed(6, 11), 6, 0.5),
    IDLE_BOTTOM(1, Range.closed(18, 23), 6, 0.5),
    IDLE_LEFT(1, Range.closed(12, 17), 6, 0.5),
    IDLE_RIGHT(1, Range.closed(0, 5), 6, 0.5),

    WALK_TOP(2, Range.closed(6, 11), 6, 0.5),
    WALK_BOTTOM(2, Range.closed(18, 23), 6, 0.5),
    WALK_LEFT(2, Range.closed(12, 17), 6, 0.5),
    WALK_RIGHT(2, Range.closed(0, 5), 6, 0.5),

    PLATE_IDLE_TOP(3, Range.closed(6, 11), 6, 0.5, true, false, 32, 32),
    PLATE_IDLE_BOTTOM(3, Range.closed(18, 23), 6, 0.5, true, false, 32, 32),
    PLATE_IDLE_LEFT(3, Range.closed(12, 17), 6, 0.5, true, false, 32, 32),
    PLATE_IDLE_RIGHT(3, Range.closed(0, 5), 6, 0.5, true, false, 32, 32),

    PLATE_WALK_TOP(4, Range.closed(6, 11), 6, 0.5, true, false, 32, 32),
    PLATE_WALK_BOTTOM(4, Range.closed(18, 23), 6, 0.5, true, false, 32, 32),
    PLATE_WALK_LEFT(4, Range.closed(12, 17), 6, 0.5, true, false, 32, 32),
    PLATE_WALK_RIGHT(4, Range.closed(0, 5), 6, 0.5, true, false, 32, 32);

    private final int rowIndex;
    private final Range<Integer> columnRange;
    private final int frames;
    private final double duration;
    private final boolean loop;
    private final boolean reverse;
    private final int frameWidth;
    private final int frameHeight;

    CharacterAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration) {
        this(rowIndex, columnRange, frames, duration, true);
    }

    CharacterAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
        this(rowIndex, columnRange, frames, duration, loop, false);
    }

    CharacterAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
        this(rowIndex, columnRange, frames, duration, loop, reverse, 0, 0);
    }

    CharacterAnimationState(int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse, int frameWidth, int frameHeight) {
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
