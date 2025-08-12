package com.profile.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;
import java.net.URL;

import javax.imageio.ImageIO;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.awt.*;
import java.util.List;

public class ImageManager {

    private static final Cache<String, BufferedImage> imageCache = Caffeine.newBuilder()
        .maximumSize(200)
        .softValues()
        .build();

    private static boolean imagesPreloaded = false;

    public static BufferedImage getCachedImage(String key) {
        return imageCache.get(key, k -> loadImage(k));
    }

    public static void putCachedImage(String key, BufferedImage image) {
        imageCache.put(key, image);
    }

    public static BufferedImage loadImageFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            return ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("Failed to load image from URL: " + imageUrl);
            e.printStackTrace();
            return null;
        }
    }

    private static BufferedImage loadImage(String key) {
        try (InputStream is = ImageManager.class.getResourceAsStream("/images/" + key + ".png")) {
            if (is == null) {
                System.err.println("Image not found: /images/" + key + ".png");
                return null;
            }
            return ImageIO.read(is);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + key);
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized void preloadAllImages() {
        if (imagesPreloaded) return;

        try {
            List<String> imageKeys = loadImageKeysFromIndex();
            preloadImages(imageKeys);
            imagesPreloaded = true;
            System.out.println("All troop images preloaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to preload some images:");
            e.printStackTrace();
        }
    }

    private static void preloadImages(List<String> imageKeys) throws InterruptedException {
        Set<String> uniqueKeys = new HashSet<>(imageKeys);
        List<String> uniqueList = new ArrayList<>(uniqueKeys);
        int batchSize = 10;

        ExecutorService executor = Executors.newFixedThreadPool(batchSize);

        try {
            for (int i = 0; i < uniqueList.size(); i += batchSize) {
                List<String> batch = uniqueList.subList(i, Math.min(i + batchSize, uniqueList.size()));

                List<Callable<Void>> tasks = new ArrayList<>();
                for (String key : batch) {
                    tasks.add(() -> {
                        getCachedImage(key);
                        return null;
                    });
                }

                try {
                    executor.invokeAll(tasks);
                } catch (Exception e) {
                    System.err.println("Failed to preload batch " + i + "-" + (i + batchSize) + ":");
                    e.printStackTrace();
                }

                if (i + batchSize < uniqueList.size()) {
                    Thread.sleep(100);
                }
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES); 
        }
    }

    public static List<String> loadImageKeysFromIndex() {
        List<String> keys = new ArrayList<>();
        try (InputStream is = ImageManager.class.getResourceAsStream("/cache/images.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    keys.add(line.replace(".png", ""));
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Could not load /cache/images.txt");
            e.printStackTrace();
        }
        return keys;
    }

    public static void setupCanvasContext(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static BufferedImage getTownhallImage(int townhallLevel) {
        int townhallCapped = Math.min(17, townhallLevel);
        return getCachedImage("Building_HV_Town_Hall_level_" + townhallCapped);
    }

    public static BufferedImage getAchievementStarsImage(int achievementStars) {
        return getCachedImage(achievementStars + "star");
    }

    public static void drawSignature(Graphics2D g, int x, int y, int outline) {
        BufferedImage creatorLogo = getCachedImage("CreatorLogo");

        g.drawImage(creatorLogo, x + 18, y + 26, 53, 53, null);

        FontUtils.drawClashFont(g, "Azer", x + 88, y + 30, 53, false, Color.WHITE, outline);
    }
}
