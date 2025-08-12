package com.profile.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.profile.service.imageGenerator.ImageGenerator;

import java.util.concurrent.TimeUnit;

public class ImageFactory {

    private static final Cache<String, byte[]> renderCache = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .maximumSize(30)
        .build();

    public static <U> byte[] getCachedRender(
        String renderClass,
        String tag,
        U data,
        ImageGenerator<U> renderFunction
    ) throws Exception {
        String key = renderClass + "-" + tag;

        try {
            return renderCache.get(key, k -> {
                try {
                    return renderFunction.generateImage(data);
                } catch (Exception e) {
                    System.err.println("Render function failed for key " + key + ": " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            throw e;
        }
    }
}