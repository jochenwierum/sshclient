package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.gfx.ColorFactory;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;

public class AWTColorFactory implements ColorFactory {
    private static final Logger LOGGER = Logger
            .getLogger(AWTColorFactory.class);

    private class SystemColor implements TerminalColor {
        private final ColorName colorName;
        private final boolean isForeground;
        private final boolean isBright;

        public SystemColor(final ColorName colorName,
                final boolean isForeground, final boolean isBright) {
            this.colorName = colorName;
            this.isForeground = isForeground;
            this.isBright = isBright;
        }

        @Override
        public Color getColor() {
            return gfxInfo.mapColor(colorName, isBright);
        }

        @Override
        public boolean isColor(final ColorName color) {
            return color == this.colorName;
        }

        @Override
        public boolean isForeground() {
            return isForeground;
        }
    }

    private static class CustomColor implements TerminalColor {
        private final int colorCode;
        private final boolean isForeground;
        private final Color color;

        public CustomColor(final int colorCode, final Color color, final boolean isForeground) {
            this.colorCode = colorCode;
            this.isForeground = isForeground;
            this.color = color;
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public boolean isColor(final ColorName color) {
            return false;
        }

        @Override
        public boolean isForeground() {
            return isForeground;
        }
    }

    private final AWTGfxInfo gfxInfo;
    private final Map<Integer, Color> customColors;
    private boolean createBrightColors;

    public AWTColorFactory(final AWTGfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        this.customColors = new HashMap<Integer, Color>();
        initDefaultColors();

    }

    private void initDefaultColors() {
        for (int red = 0; red < 6; red++) {
            for (int green = 0; green < 6; green++) {
                for (int blue = 0; blue < 6; blue++) {
                    final int colorKey = (red * 36) + (green * 6) + blue + 16;
                    customColors.put(colorKey, new Color(
                            (red > 0 ? (red * 40 + 55) : 0),
                            (green > 0 ? (green * 40 + 55) : 0),
                            (blue > 0 ? (blue * 40 + 55) : 0)));
                }
            }
        }

        final int NUMBER_OF_COLORS = 232;
        for (int i = 0; i < 24; i++) {
            final int rgb = (i * 10) + 8;
            customColors.put(NUMBER_OF_COLORS + i, new Color(rgb, rgb, rgb));
        }
    }

    @Override
    public TerminalColor createStandardColor(final int colorCode) {
        final ColorName color = ColorName.find(colorCode);
        if (color != null) {
            final boolean isForeground = ColorName.isForeground(colorCode);
            return new SystemColor(color, isForeground,
                    createBrightColors && isForeground);
        }
        return null;
    }

    @Override
    public TerminalColor createStandardColor(final ColorName color, final boolean isForeground) {
        return new SystemColor(color, isForeground, createBrightColors && isForeground);
    }

    @Override
    public TerminalColor createCustomColor(final int colorCode, final boolean isForeground) {
        if (colorCode <= 16) {
            final int systemColorCode = colorCode % 8 + ColorName.BLACK.ordinal();
            final boolean isBright = colorCode < 8;
            return new SystemColor(ColorName.values()[systemColorCode],
                    isForeground, isBright);
        }
        return new CustomColor(colorCode, customColors.get(colorCode), isForeground);
    }

    @Override
    public void updateCustomColor(final int colorCode,
            final int red, final int green, final int blue) {
        LOGGER.debug("Setting new cucstom color " + colorCode + " to "
                + red + "/" + green + "/" + blue);
        customColors.put(colorCode, new Color(red, green, blue));
    }

    public void createBrightColors(final boolean brightColors) {
        this.createBrightColors = brightColors;
    }

    public TerminalColor updateColor(final TerminalColor color) {
        if(color instanceof SystemColor) {
            final SystemColor systemColor = (SystemColor) color;
            return new SystemColor(systemColor.colorName,
                    systemColor.isForeground, createBrightColors);
        } else {
            final CustomColor customColor = (CustomColor) color;
            return new CustomColor(customColor.colorCode,
                    customColors.get(customColor.colorCode),
                    customColor.isForeground);
        }
    }
}