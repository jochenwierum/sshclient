package de.jowisoftware.sshclient.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class FontUtils {
    private FontUtils() { /* util class */ }

    private static final List<String> fontCache = new ArrayList<>();
    private static final List<KeyValue<String, Object>> renderingHints = new ArrayList<>();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isMonospacedFont(final Font font) {
        final BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics graphics = bufferedImage.createGraphics();

        final boolean result = isMonospacedFont(font, graphics);

        graphics.dispose();

        return result;
    }

    private static boolean isMonospacedFont(final Font font, final Graphics graphics) {
        final FontMetrics metrics = graphics.getFontMetrics(font.deriveFont(12f));

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

    public static String[] getCachedMonospacedFonts() {
        synchronized (fontCache) {
            return fontCache.toArray(new String[fontCache.size()]);
        }
    }

    public static void fillAsyncCache() {
        new Thread() {
            @Override
            public void run() {
                fillFontCache();
            }
        }.start();
    }

    private static void fillFontCache() {
        synchronized(fontCache) {
            fontCache.clear();
            final String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

            final BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            final Graphics graphics = bufferedImage.createGraphics();

            for (final String font : fontList) {
                if (isMonospacedFont(new Font(font, 0, 12), graphics)) {
                    fontCache.add(font);
                }
            }

            graphics.dispose();
        }
    }

    public static List<KeyValue<String, Object>> getRenderingHintMap() {
        if (renderingHints.isEmpty()) {
            fillRenderingHints();
        }
        return new ArrayList<>(renderingHints);
    }

    private static void fillRenderingHints() {
        renderingHints.add(new KeyValue<>("automatic", RenderingHints.VALUE_TEXT_ANTIALIAS_GASP));
        renderingHints.add(new KeyValue<>("off", RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));
        renderingHints.add(new KeyValue<>("on", RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        renderingHints.add(new KeyValue<>("hrgb", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB));
        renderingHints.add(new KeyValue<>("hbgr", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR));
        renderingHints.add(new KeyValue<>("vrgb", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB));
        renderingHints.add(new KeyValue<>("vbgr", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR));
    }
}
