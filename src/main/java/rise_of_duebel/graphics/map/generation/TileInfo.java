package rise_of_duebel.graphics.map.generation;

public record TileInfo<T, Q>(T state, Q type, TileCoord coord, double noise, int heightMap) {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TileInfo && ((TileInfo) obj).state() == this.state && ((TileInfo) obj).heightMap() == this.heightMap) {
            return true;
        }
        return false;
    }
}
