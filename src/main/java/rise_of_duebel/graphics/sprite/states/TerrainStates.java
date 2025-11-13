package rise_of_duebel.graphics.sprite.states;

import rise_of_duebel.graphics.sprite.ISheetState;

public enum TerrainStates implements ISheetState {
    GRASS_1(0, 0),
    GRASS_2(0, 1),
    GRASS_3(0, 2),
    GRASS_4(0, 3),
    GRASS_5(0, 4),
    GRASS_6(0, 5),
    GRASS_7(0, 6),
    GRASS_8(0, 7),
    GRASS_9(0, 8),

    GRASS_11(1, 0),
    GRASS_12(1, 1),
    GRASS_13(1, 2),
    GRASS_14(1, 3),
    GRASS_15(1, 4),
    GRASS_16(1, 5),
    GRASS_17(1, 6),
    GRASS_18(1, 7),
    GRASS_19(1, 8);

    private final int rowIndex;
    private final int columnIndex;
    private final int frameWidth;
    private final int frameHeight;
    
    private static final int DEFAULT_FRAME_WIDTH = 32;
    private static final int DEFAULT_FRAME_HEIGHT = 32;
    private static final TerrainStates[] grass = new TerrainStates[] {
        TerrainStates.GRASS_1,
        TerrainStates.GRASS_2,
        TerrainStates.GRASS_3,
        TerrainStates.GRASS_4,
        TerrainStates.GRASS_5,
        TerrainStates.GRASS_6,
        TerrainStates.GRASS_7,
        TerrainStates.GRASS_8,
        TerrainStates.GRASS_9,
        TerrainStates.GRASS_11,
        TerrainStates.GRASS_12,
        TerrainStates.GRASS_13,
        TerrainStates.GRASS_14,
        TerrainStates.GRASS_15,
        TerrainStates.GRASS_16,
        TerrainStates.GRASS_17,
        TerrainStates.GRASS_18,
        TerrainStates.GRASS_19,
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
        return TerrainStates.grass[(int) Math.round(Math.random() * 17)];
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

