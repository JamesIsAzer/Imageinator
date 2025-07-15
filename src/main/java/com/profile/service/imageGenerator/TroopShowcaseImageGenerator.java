package com.profile.service.imageGenerator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.profile.data.Profile;
import com.profile.render.TroopShowcaseImageRenderer;
import com.profile.utils.RenderLimiter;

public class TroopShowcaseImageGenerator implements ImageGenerator<Profile>{

    @Override
    public byte[] generateImage(Profile profile) throws Exception {
        if (!RenderLimiter.SEMAPHORE.tryAcquire(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Too many concurrent renders.");
        }

        BufferedImage image = null;
        try {
            image = TroopShowcaseImageRenderer.render(profile);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", baos);
                return baos.toByteArray();
            }
        } finally {
            if (image != null) {
                image.flush();
            }
            RenderLimiter.SEMAPHORE.release();
        }
        
    }
}
