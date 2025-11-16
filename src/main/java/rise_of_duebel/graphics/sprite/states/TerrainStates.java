package rise_of_duebel.graphics.sprite.states;

import rise_of_duebel.graphics.sprite.ISheetState;
import rise_of_duebel.utils.MathUtils;

public enum TerrainStates implements ISheetState {
    GRASS_CORNER_LT_1(0, 1),
    GRASS_CORNER_LT_2(1, 1),

    GRASS_CORNER_T_1(0, 2),
    GRASS_CORNER_T_2(1, 2),

    GRASS_CORNER_RT_1(0, 3),
    GRASS_CORNER_RT_2(1, 3),

    GRASS_CORNER_L(2, 1),

    GRASS_M(2, 2),

    GRASS_CORNER_R(2, 3),

    GRASS_CORNER_LB_1(3, 1),
    GRASS_CORNER_LB_2(4, 1),

    GRASS_CORNER_B_1(3, 2),
    GRASS_CORNER_B_2(4, 2),

    GRASS_CORNER_RB_1(3, 3),
    GRASS_CORNER_RB_2(4, 3),
    ;

    private final int rowIndex;
    private final int columnIndex;
    private final int frameWidth;
    private final int frameHeight;
    
    private static final int DEFAULT_FRAME_WIDTH = 16;
    private static final int DEFAULT_FRAME_HEIGHT = 16;
    private static final TerrainStates[] grass = new TerrainStates[] {
        TerrainStates.GRASS_M,
    };

    TerrainStates(int rowIndex, int columnIndex) {
        this(rowIndex, columnIndex, TerrainStates.DEFAULT_FRAME_WIDTH, TerrainStates.DEFAULT_FRAME_HEIGHT); 
    }
    
    TerrainStates(int rowIndex, int columnIndex, int frameWidth, int frameHeight) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public static TerrainStates getRandomGrass() {
        return TerrainStates.grass[(int) MathUtils.clamp(Math.round(Math.random() * grass.length), 0, TerrainStates.grass.length - 1)];
    }

    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    @Override
    public int getColumnIndex() {
        return this.columnIndex;
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

