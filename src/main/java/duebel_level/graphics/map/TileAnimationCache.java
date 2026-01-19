package duebel_level.graphics.map;

import java.awt.image.BufferedImage;

public record TileAnimationCache(long tileId, BufferedImage[] frames, int[] durations, int totalDuration) {}
