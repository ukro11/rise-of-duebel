package rise_of_duebel.graphics.map.generation;

public record TileCoord(double x, double y) {

    public TileCoord above() {
        return new TileCoord(this.x(), this.y() - 1);
    }

    public TileCoord left() {
        return new TileCoord(this.x() - 1, this.y());
    }

    public TileCoord right() {
        return new TileCoord(this.x() + 1, this.y());
    }

    public TileCoord below() {
        return new TileCoord(this.x(), this.y() - 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TileCoord && ((TileCoord) obj).x == this.x && ((TileCoord) obj).y == this.y) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Tile(%.2f|%.2f)", this.x, this.y);
    }
}