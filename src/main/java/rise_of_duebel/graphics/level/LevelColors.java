package rise_of_duebel.graphics.level;

import java.awt.*;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;

public record LevelColors(Color background, Color accent, Color world, Color moving) {

    public LevelColors(String background, String accent, String world, String moving, String portal) { this(Color.decode(background), Color.decode(accent), Color.decode(world), Color.decode(moving)); }

    public static LevelColors createDefault() {
        return new LevelColors("#f4b13b", "#feab32", "#be7708", "#be7708", "#6603fc");
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
