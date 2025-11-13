package rise_of_duebel.graphics.sprite;

public interface ISheetState {
    int getRowIndex();
    int getColumnIndex();
    int getFrameWidth();
    int getFrameHeight();

    static <T extends Enum<T> & ISheetState> T fetch(Class<T> enumClass, int row, int column) {
        if (!enumClass.isEnum()) return null;
        for (var state : enumClass.getEnumConstants()) {
            if (state.getRowIndex() == row && state.getColumnIndex() == column) {
                return state;
            }
        }
        return null;
    }
}
