package com.profile.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class BlurUtils {

    private static final Cache<ShadowKey, BufferedImage> shadowCache = Caffeine.newBuilder()
        .maximumSize(100) // or however many you think is reasonable
        .expireAfterAccess(5, TimeUnit.MINUTES) // optional
        .build();
        
    public static void drawDropShadow(Graphics2D g, int x, int y, int width, int height, int cornerRadius, float blurIntensity) {
        int blurRadius = (int)(cornerRadius * blurIntensity);  // blur size follows curvature
        int offsetX = 4;
        int offsetY = 4;

        ShadowKey key = new ShadowKey(width, height, cornerRadius, blurRadius);

        BufferedImage shadowImage = shadowCache.get(key, k -> {
            int paddedWidth = width + blurRadius * 4;
            int paddedHeight = height + blurRadius * 4;

            BufferedImage base = new BufferedImage(paddedWidth, paddedHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gShadow = base.createGraphics();
            gShadow.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int shapeX = (paddedWidth - width) / 2;
            int shapeY = (paddedHeight - height) / 2;

            gShadow.setColor(new Color(0, 0, 0, 100));
            gShadow.fill(new RoundRectangle2D.Float(
                shapeX, shapeY, width, height, cornerRadius, cornerRadius
            ));
            gShadow.dispose();

            return blurImage(base, blurRadius);
        });

        // Draw shadow centered under the target shape
        int drawX = x - (shadowImage.getWidth() - width) / 2 + offsetX;
        int drawY = y - (shadowImage.getHeight() - height) / 2 + offsetY;

        g.drawImage(shadowImage, drawX, drawY, null);
    }

    // Key class used for caching
    private static class ShadowKey {
        final int width, height, cornerRadius, blurRadius;

        ShadowKey(int width, int height, int cornerRadius, int blurRadius) {
            this.width = width;
            this.height = height;
            this.cornerRadius = cornerRadius;
            this.blurRadius = blurRadius;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShadowKey)) return false;
            ShadowKey k = (ShadowKey) o;
            return width == k.width &&
                   height == k.height &&
                   cornerRadius == k.cornerRadius &&
                   blurRadius == k.blurRadius;
        }

        @Override
        public int hashCode() {
            return Objects.hash(width, height, cornerRadius, blurRadius);
        }
    }

    public static BufferedImage blurImage(BufferedImage src, int radius) {
        int size = radius * 2 + 1;
        float[] data = createGaussianKernel(radius);
        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(src, null);
    }

    // Generates a 1D Gaussian kernel and turns it into 2D
    private static float[] createGaussianKernel(int radius) {
        int size = radius * 2 + 1;
        float[] kernel = new float[size * size];
        float sigma = radius / 3.0f;
        float sum = 0f;

        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                float value = (float) Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[(y + radius) * size + (x + radius)] = value;
                sum += value;
            }
        }

        // Normalize
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }
}
