package com.profile.utils;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FontUtils {

    public static void loadCustomFonts() {
        try {
            registerFont("/fonts/Clash_Regular.otf");
            registerFont("/fonts/Clash_Bold.otf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerFont(String path) throws Exception {
        try (InputStream is = FontUtils.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Font not found at: " + path);
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        }
    }

    public static void drawText(Graphics2D g, String text, int x, int y, boolean centered) {
        FontMetrics metrics = g.getFontMetrics();
        int drawX = x;
        int drawY = y;

        if (centered) {
            int textWidth = metrics.stringWidth(text);
            int textHeight = metrics.getAscent() - metrics.getDescent();
            drawX = x - textWidth / 2;
            drawY = y + textHeight / 2;
        }

        g.drawString(text, drawX, drawY);
    }
    
    public static void drawClashFont(Graphics2D g, String message, int x, int y, int fontSize, boolean centered, Color colour, int borderSize) {
        g.setFont(new Font("Clash", Font.PLAIN, fontSize));

        FontMetrics metrics = g.getFontMetrics();
        int drawX = x;
        int drawY = y;

        if (centered) {
            int textWidth = metrics.stringWidth(message);
            int textHeight = metrics.getAscent() + metrics.getDescent();
            
            drawX = x - textWidth / 2;
            drawY = y + (metrics.getAscent() - textHeight / 2);
        }
        else {
            drawY = y + metrics.getAscent();
        }

        // Shadow
        g.setColor(Color.BLACK);
        g.drawString(message, drawX, drawY + borderSize);

        // Border (stroke)
        g.setStroke(new BasicStroke(borderSize));
        g.setColor(Color.BLACK);
        g.draw(new TextLayout(message, g.getFont(), g.getFontRenderContext()).getOutline(AffineTransform.getTranslateInstance(drawX, drawY)));

        // Main text
        g.setColor(colour);
        g.drawString(message, drawX, drawY);
    }

    public static void clashFontScaled(Graphics2D g, String message, int x, int y, int maxWidth, int maxHeight, boolean centered) {
        int fontSize = maxHeight;
        Font baseFont = new Font("ClashFont", Font.PLAIN, fontSize);
        FontMetrics metrics = g.getFontMetrics(baseFont);

        while (true) {
            metrics = g.getFontMetrics(baseFont.deriveFont((float) fontSize));
            int textWidth = metrics.stringWidth(message);
            int textHeight = metrics.getAscent() + metrics.getDescent();

            if (textWidth <= maxWidth && textHeight <= maxHeight) {
                break;
            }

            fontSize--;
            if (fontSize <= 5) break;
        }

        // Draw with final font size
        drawClashFont(g, message, x, y, fontSize, centered, Color.WHITE, 2);
    }

    public static String formatNumberWithSpaces(int number) {
        return String.format("%,d", number).replace(',', ' ');
    }

    public static String formatPlace(Integer number) {
        if (number <= 0) return String.valueOf(number);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);

        int mod100 = number % 100;
        String suffix;

        if (mod100 >= 11 && mod100 <= 13) {
            suffix = "th";
        } else {
            switch (number % 10) {
                case 1:
                    suffix = "st";
                    break;
                case 2:
                    suffix = "nd";
                    break;
                case 3:
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
                    break;
            }
        }

        return formatter.format(number) + suffix + " place";
    }
}