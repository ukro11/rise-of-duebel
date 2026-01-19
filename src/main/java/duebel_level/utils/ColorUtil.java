package duebel_level.utils;

import java.awt.*;

public class ColorUtil {

    /**
     * Interpoliert zwischen zwei Farben.
     *
     * @param from Startfarbe
     * @param to   Zielfarbe
     * @param t    0.0 -> from, 1.0 -> to
     */
    public static Color lerp(Color from, Color to, double t) {
        t = clamp01(t);

        int r = (int) (from.getRed()   + (to.getRed()   - from.getRed())   * t);
        int g = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * t);
        int b = (int) (from.getBlue()  + (to.getBlue()  - from.getBlue())  * t);
        int a = (int) (from.getAlpha() + (to.getAlpha() - from.getAlpha()) * t);

        return new Color(r, g, b, a);
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
