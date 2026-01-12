package rise_of_duebel.graphics.level;

import java.awt.*;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;

public record LevelColors(Color background, Color world, Color moving) {

    public LevelColors(String background, String world, String moving, String portal) { this(Color.decode(background), Color.decode(world), Color.decode(moving)); }

    public static LevelColors createDefault() {
        return new LevelColors("#f4b13b", "#be7708", "#be7708", "#6603fc");
    }

    @Override
    public Color background() {
        return this.background;
    }

    @Override
    public Color world() {
        return this.world;
    }

    @Override
    public Color moving() {
        return this.moving;
    }

    public Color getColorByLayer(String layer) {
        try {
            for (RecordComponent component : this.getClass().getRecordComponents()) {
                if (component.getName().equalsIgnoreCase(layer)) {
                    Method accessor = component.getAccessor();
                    return (Color) accessor.invoke(this);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s does not exist.", layer), e);
        }
        return null;
    }
}
