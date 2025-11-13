package rise_of_duebel.graphics.map.generation;

import KAGO_framework.view.DrawTool;
import rise_of_duebel.graphics.sprite.SpritesheetRenderer;
import rise_of_duebel.graphics.sprite.states.TerrainStates;
import rise_of_duebel.utils.MathUtils;

import java.awt.*;
import java.util.HashMap;

public class GeneratedMap {

    private final Noise noise;
    private final int width = 2000;
    private final int height = 2000;
    private final SpritesheetRenderer<TerrainStates> renderer;
    private final HashMap<TileCoord, TerrainStates> tileset = new HashMap<>();

    private record TileCoord(double x, double y) {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TileCoord && ((TileCoord) obj).x == this.x && ((TileCoord) obj).y == this.y) {
                return true;
            }
            return false;
        }
    }

    public GeneratedMap() {
        this.noise = new Noise();
        this.renderer = new SpritesheetRenderer<>("/graphic/temp/TX Tileset Grass.png", 8, 8, TerrainStates.class);
    }

    public void draw(DrawTool drawTool) {
        int scale = 32;
        for (int i = 0; i < this.width / scale; i++) {
            for (int j = 0; j < this.height / scale; j++) {
                double n = this.noise.noise(i, j);
                //drawTool.setCurrentColor(new Color(this.biomeColor(n)));
                //drawTool.drawFilledRectangle(scale * i, scale * j, scale, scale);
                this.biomeColor(n, i * 32, j * 32, drawTool);
            }
        }
    }

    /*private int biomeColor(double v) {
        Color c;
        if (v < 0.25) {
            c = lerpColor(hex(0x081b2c), hex(0x0b2c4e), smoothstep(0.00, 0.25, v)); // tiefes, kaltes Wasser
        } else if (v < 0.45) {
            c = lerpColor(hex(0x12324a), hex(0x174b3a), smoothstep(0.25, 0.45, v)); // sumpfig-bläulich/grün
        } else if (v < 0.60) {
            c = lerpColor(hex(0x1b3e2b), hex(0x2c5b3c), smoothstep(0.45, 0.60, v)); // dunkle Wiese
        } else if (v < 0.78) {
            c = lerpColor(hex(0x1a2f1f), hex(0x213d28), smoothstep(0.60, 0.78, v)); // dichter Wald
        } else {
            c = lerpColor(hex(0x3b3b44), hex(0x032036), smoothstep(0.78, 1.00, v));  // Fels -> kühler Schnee
        }

        // dezente blaue Glows an „magischen Quellen“ (prozedural)
        // nutzt ein paar zusätzliche Noise-Samples als Lichtverstärker
        double glow = Math.max(0, this.hashToUnit(this.fastHash((int)(v*9973))) - 0.7) * 3.3;
        int r = clamp255((int)(c.getRed()   * (1.0 - 0.10*glow)));
        int g = clamp255((int)(c.getGreen() * (1.0 - 0.10*glow)));
        int b = clamp255((int)(c.getBlue()  + 35*glow));
        return (0xFF << 24) | (r<<16) | (g<<8) | b;
    }*/

    private void biomeColor(double v, double x, double y, DrawTool drawTool) {
        Color c;
        if (v < 0.25) {
            c = lerpColor(hex(0x081b2c), hex(0x0b2c4e), smoothstep(0.00, 0.25, v)); // tiefes, kaltes Wasser
            drawTool.setCurrentColor(Color.blue);
            drawTool.drawFilledRectangle(x, y, 32, 32);

        } else if (v < 0.45) {
            c = lerpColor(hex(0x12324a), hex(0x174b3a), smoothstep(0.25, 0.45, v)); // sumpfig-bläulich/grün
            drawTool.setCurrentColor(Color.red);
            drawTool.drawFilledRectangle(x, y, 32, 32);

        } else if (v < 0.60) {
            c = lerpColor(hex(0x1b3e2b), hex(0x2c5b3c), smoothstep(0.45, 0.60, v)); // dunkle Wiese
            this.tileset.putIfAbsent(new TileCoord(x, y), TerrainStates.getRandomGrass());
            this.renderer.renderSprite(this.tileset.get(new TileCoord(x, y)), x, y, drawTool);

        } else if (v < 0.78) {
            c = lerpColor(hex(0x1a2f1f), hex(0x213d28), smoothstep(0.60, 0.78, v)); // dichter Wald
            //drawTool.setCurrentColor(Color.green);
            //drawTool.drawFilledRectangle(x, y, 32, 32);
            this.tileset.putIfAbsent(new TileCoord(x, y), TerrainStates.getRandomGrass());
            this.renderer.renderSprite(this.tileset.get(new TileCoord(x, y)), x, y, drawTool);

        } else {
            c = lerpColor(hex(0x3b3b44), hex(0x032036), smoothstep(0.78, 1.00, v));  // Fels -> kühler Schnee
            drawTool.setCurrentColor(Color.magenta);
            drawTool.drawFilledRectangle(x, y, 32, 32);
        }
    }

    private Color lerpColor(Color a, Color b, double t) {
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl= (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(clamp255(r), clamp255(g), clamp255(bl));
    }

    private int clamp255(int v) { return Math.max(0, Math.min(255, v)); }

    private Color hex(int rgb) { return new Color((rgb>>16)&255, (rgb>>8)&255, rgb&255); }

    private double smoothstep(double a, double b, double x) {
        x = MathUtils.clamp((x - a) / (b - a), 0.0, 1.0);
        return x * x * (3 - 2 * x);
    }

    private int fastHash(int x) {
        x ^= (x << 13);
        x ^= (x >>> 17);
        x ^= (x << 5);
        return x;
    }

    private double hashToUnit(int h) {
        // map signed int to [0,1]
        return (h - (double)Integer.MIN_VALUE) / (double)0xFFFFFFFFL;
    }

    public void drawAfterPlayer(DrawTool drawTool) {

    }
}
