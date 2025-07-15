package com.profile.service.imageGenerator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.profile.data.Profile;
import com.profile.render.XpImageRenderer;

public class XpImageGenerator implements ImageGenerator<Profile> {

    @Override
    public byte[] generateImage(Profile profile) throws Exception {
        BufferedImage image = null;
        try {
            image = XpImageRenderer.render(profile);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", baos);
                return baos.toByteArray();
            }
        } finally {
            if (image != null) {
                image.flush();
            }
        }
    }
}