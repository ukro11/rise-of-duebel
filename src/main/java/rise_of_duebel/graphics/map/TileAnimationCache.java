package rise_of_duebel.graphics.map;

import java.awt.image.BufferedImage;

public record TileAnimationCache(long tileId, BufferedImage[] frames, int[] durations, int totalDuration) {}
