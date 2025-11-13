package rise_of_duebel.graphics.map;

import KAGO_framework.view.DrawTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapManager {

    private Logger logger = LoggerFactory.getLogger(MapManager.class);
    private final CopyOnWriteArrayList<TileMap> maps = new CopyOnWriteArrayList<>();
    private TileMap loadedMap;

    public MapManager() {}

    public MapManager(TileMap map) {
        this.loadedMap = map;
    }

    public MapManager(int index, TileMap... maps) {
        this.maps.addAll(List.of(maps));
        this.loadedMap = maps[index];
    }

    public void showMap(int index) {
        var map = this.maps.get(index);
        if (map == null) {
            this.logger.error("The map with index {} could not be shown", index);

        } else {
            this.loadedMap = map;
        }
    }

    public void showMap(String filename) {
        var map = this.maps.stream().filter(m -> m.getFileName().equals(filename)).findFirst();
        map.ifPresentOrElse((m) -> this.loadedMap = m, () -> this.logger.error("The map {} could not be shown", filename));
    }

    public void showMap(TileMap map, boolean delete) {
        if (map == null) {
            this.logger.error("The map could not be shown");

        } else {
            if (!this.maps.contains(map)) {
                this.maps.add(map);
            }
            if (delete) {
                this.maps.remove(this.loadedMap);
            }
            this.loadedMap = map;
        }
    }

    public void importMap(TileMap map) {
        this.maps.add(map);
    }

    public void draw(DrawTool drawTool) {
        if (this.loadedMap != null) {
            this.loadedMap.draw(drawTool);
        }
    }

    public void drawAfterPlayer(DrawTool drawTool) {
        if (this.loadedMap != null) {
            this.loadedMap.drawAfterPlayer(drawTool);
        }
    }

    public TileMap getLoadedMap() {
        return this.loadedMap;
    }
}
