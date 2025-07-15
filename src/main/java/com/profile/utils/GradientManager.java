package com.profile.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class GradientManager {
    private static final Cache<String, Paint> gradientCache = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .maximumSize(50)
        .build();

    public static Paint createOptimizedGradient(
        String key, 
        int x, 
        int y, 
        int width, 
        int height,
        Color[] colors, 
        float[] offsets, 
        boolean horizontal
    ) {
        String cacheKey = key + "_" + width + "_" + height + "_" +
                        (horizontal ? "horizontal" : "vertical") + "_" + generateColorKey(colors, offsets);

        return gradientCache.get(cacheKey, k -> {
            if (horizontal) {
                return new LinearGradientPaint(x, y, x + width, y, offsets, colors);
            } else {
                return new LinearGradientPaint(x, y, x, y + height, offsets, colors);
            }
        });
    }

    private static String generateColorKey(Color[] colors, float[] offsets) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colors.length; i++) {
            Color c = colors[i];
            sb.append(offsets[i]).append("_").append(c.getRed()).append("_").append(c.getGreen())
              .append("_").append(c.getBlue()).append("_").append(c.getAlpha()).append("|");
        }
        return sb.toString();
    }
}
