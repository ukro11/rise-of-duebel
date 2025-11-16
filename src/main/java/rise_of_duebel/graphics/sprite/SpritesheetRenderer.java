package rise_of_duebel.graphics.sprite;

import KAGO_framework.view.DrawTool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class SpritesheetRenderer<T extends Enum<T> & ISheetState> {

    private BufferedImage sheet;
    private HashMap<T, BufferedImage> sprites;

    public SpritesheetRenderer(String spriteSheetPath, int rows, int columns, Class<T> clazz) {
        try {
            this.sprites = new HashMap<>();
            this.sheet = ImageIO.read(SpritesheetRenderer.class.getResource(spriteSheetPath));
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    var state = ISheetState.fetch(clazz, i, j);
                    if (state == null) continue;
                    this.sprites.put(state, this.sheet.getSubimage(j * state.getFrameWidth(), i * state.getFrameHeight(), state.getFrameWidth(), state.getFrameHeight()));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void renderSprite(T state, double x, double y, DrawTool drawTool) {
        this.renderSprite(state, x, y, state.getFrameWidth(), state.getFrameHeight(), drawTool);
    }

    public void renderSprite(T state, double x, double y, double width, double height, DrawTool drawTool) {
        drawTool.getGraphics2D().drawImage(this.sprites.get(state), (int) x, (int) y, (int) width, (int) height, null, null);
    }

    public BufferedImage getSprite(T state) {
        return this.sprites.get(state);
    }

    public BufferedImage getSheet() {
        return this.sheet;
    }
}
