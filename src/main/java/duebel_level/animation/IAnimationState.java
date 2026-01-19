package duebel_level.animation;

import com.google.common.collect.Range;

import java.util.concurrent.CopyOnWriteArrayList;

public interface IAnimationState {
    int getRowIndex();
    Range<Integer> getColumnRange();
    int getFrames();
    double getDuration();
    boolean isLoop();
    boolean isReverse();
    int getFrameWidth();
    int getFrameHeight();

    static <T extends Enum<T> & IAnimationState> CopyOnWriteArrayList<T> fetch(Class<T> enumClass, int row, int column) {
        if (!enumClass.isEnum()) return new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<T> fetch = new CopyOnWriteArrayList<>();
        for (var state : enumClass.getEnumConstants()) {
            if (state.getRowIndex() == row && state.getColumnRange().contains(column)) {
                fetch.add(state);
            }
        }
        return fetch;
    }
}
