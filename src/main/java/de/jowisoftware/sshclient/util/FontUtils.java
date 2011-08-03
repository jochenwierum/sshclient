package de.jowisoftware.sshclient.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public final class FontUtils {
    private FontUtils() { /* util class */ }

    public static boolean isMonospacedFont(final Font font) {
        final BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics graphics = bufferedImage.createGraphics();

        final boolean result = isMonospacedFont(font, graphics);

        graphics.dispose();

        return result;
    }

    private static boolean isMonospacedFont(final Font font, final Graphics graphics) {
        final FontMetrics metrics = graphics.getFontMetrics(font);

        final int width = metrics.charWidth('a');
        for (char c = 'b'; c <= 'z'; ++c) {
            if (metrics.charWidth(c) != width) {
                return false;
            }
        }
        for (char c = 'A'; c <= 'Z'; ++c) {
            if (metrics.charWidth(c) != width) {
                return false;
            }
        }
        for (char c = '0'; c <= '9'; ++c) {
            if (metrics.charWidth(c) != width) {
                return false;
            }
        }
        return true;
    }
}
