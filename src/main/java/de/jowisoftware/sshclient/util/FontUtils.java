package de.jowisoftware.sshclient.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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
        final FontMetrics metrics = graphics.getFontMetrics(font.deriveFont(12));

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

    public static String[] getMonospacedFonts() {
        final List<String> result = new ArrayList<String>();
        final String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        final BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics graphics = bufferedImage.createGraphics();

        for (final String font : fontList) {
            if (isMonospacedFont(new Font(font, 0, 12), graphics)) {
                result.add(font);
            }
        }

        graphics.dispose();
        return result.toArray(new String[result.size()]);
    }
}
