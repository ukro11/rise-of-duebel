package rise_of_duebel.animation.states;

import com.google.common.collect.Range;
import rise_of_duebel.animation.entity.EntityState;
import rise_of_duebel.animation.entity.IEntityAnimationState;
import rise_of_duebel.model.entity.EntityDirection;

public enum CharacterAnimationState implements IEntityAnimationState {
    STILL_TOP(EntityDirection.TOP, EntityState.STILL, 0, Range.closed(1, 1), 1, 0.5),
    STILL_BOTTOM(EntityDirection.BOTTOM, EntityState.STILL, 0, Range.closed(3, 3), 1, 0.5),
    STILL_LEFT(EntityDirection.LEFT, EntityState.STILL, 0, Range.closed(2, 2), 1, 0.5),
    STILL_RIGHT(EntityDirection.RIGHT, EntityState.STILL, 0, Range.closed(0, 0), 1, 0.5),

    IDLE_TOP(EntityDirection.TOP, EntityState.IDLE, 1, Range.closed(6, 11), 6, 0.5),
    IDLE_BOTTOM(EntityDirection.BOTTOM, EntityState.IDLE, 1, Range.closed(18, 23), 6, 0.5),
    IDLE_LEFT(EntityDirection.LEFT, EntityState.IDLE, 1, Range.closed(12, 17), 6, 0.5),
    IDLE_RIGHT(EntityDirection.RIGHT, EntityState.IDLE, 1, Range.closed(0, 5), 6, 0.5),

    WALK_TOP(EntityDirection.TOP, EntityState.WALKING, 2, Range.closed(6, 11), 6, 0.5),
    WALK_BOTTOM(EntityDirection.BOTTOM, EntityState.WALKING, 2, Range.closed(18, 23), 6, 0.5),
    WALK_LEFT(EntityDirection.LEFT, EntityState.WALKING, 2, Range.closed(12, 17), 6, 0.5),
    WALK_RIGHT(EntityDirection.RIGHT, EntityState.WALKING, 2, Range.closed(0, 5), 6, 0.5);

    private final EntityDirection direction;
    private final EntityState state;
    private final int rowIndex;
    private final Range<Integer> columnRange;
    private final int frames;
    private final double duration;
    private final boolean loop;
    private final boolean reverse;
    private final int frameWidth;
    private final int frameHeight;

    CharacterAnimationState(EntityDirection direction, EntityState state, int rowIndex, Range<Integer> columnRange, int frames, double duration) {
        this(direction, state, rowIndex, columnRange, frames, duration, true);
    }

    CharacterAnimationState(EntityDirection direction, EntityState state, int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop) {
        this(direction, state, rowIndex, columnRange, frames, duration, loop, false);
    }

    CharacterAnimationState(EntityDirection direction, EntityState state, int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse) {
        this(direction, state, rowIndex, columnRange, frames, duration, loop, reverse, 0, 0);
    }

    CharacterAnimationState(EntityDirection direction, EntityState state, int rowIndex, Range<Integer> columnRange, int frames, double duration, boolean loop, boolean reverse, int frameWidth, int frameHeight) {
        this.direction = direction;
        this.state = state;
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
    public EntityDirection getDirection() {
        return this.direction;
    }

    @Override
    public EntityState getState() {
        return this.state;
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
