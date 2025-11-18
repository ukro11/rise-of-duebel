package rise_of_duebel.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    private static Map<String, BufferedImage> cacheImage = new ConcurrentHashMap<>();

    public static BufferedImage loadImage(String src) {
        return CacheManager.cacheImage.computeIfAbsent(src, s -> {
            try { return ImageIO.read(CacheManager.class.getResource(s)); }
            catch (IOException e) { throw new UncheckedIOException(e); }
        });
    }

    public static BufferedImage cacheImage(String id, BufferedImage image) {
        CacheManager.cacheImage.put(id, image);
        return image;
    }
}
