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
        private final boolean isBright;
        private SystemColor inverseColor = this;

        public SystemColor(final ColorName colorName,
                final boolean isBright) {
            this.colorName = colorName;
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
        public boolean isBright() {
            return isBright;
        }

        @Override
        public TerminalColor invert() {
            return inverseColor;
        }
    }

    private static class CustomColor implements TerminalColor {
        private Color color;

        public CustomColor(final Color color) {
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
        public boolean isBright() {
            return false;
        }

        @Override
        public TerminalColor invert() {
            return this;
        }
    }

    private final AWTGfxInfo gfxInfo;
    private final Map<Integer, CustomColor> customColors =
            new HashMap<Integer, CustomColor>();
    private final Map<Integer, SystemColor> systemColors =
            new HashMap<Integer, SystemColor>();

    public AWTColorFactory(final AWTGfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        initSystemColors();
        initCustomColors();
    }

    private void initSystemColors() {
        final int colorCount = ColorName.values().length;
        for (int i = 0; i < colorCount; ++i) {
            systemColors.put(i,
                    new SystemColor(ColorName.values()[i], false));
        }
        for (int i = 0; i < colorCount; ++i) {
            systemColors.put(colorCount + i,
                    new SystemColor(ColorName.values()[i], true));
        }

        systemColors.get(0).inverseColor = systemColors.get(1);
        systemColors.get(1).inverseColor = systemColors.get(0);
        systemColors.get(colorCount).inverseColor = systemColors.get(colorCount + 1);
        systemColors.get(colorCount + 1).inverseColor = systemColors.get(colorCount);
    }

    private void initCustomColors() {
        for (int red = 0; red < 6; red++) {
            for (int green = 0; green < 6; green++) {
                for (int blue = 0; blue < 6; blue++) {
                    final int colorKey = (red * 36) + (green * 6) + blue + 16;
                    customColors.put(colorKey,
                            new CustomColor(
                            new Color(
                            (red > 0 ? (red * 40 + 55) : 0),
                            (green > 0 ? (green * 40 + 55) : 0),
                            (blue > 0 ? (blue * 40 + 55) : 0))));
                }
            }
        }

        final int NUMBER_OF_COLORS = 232;
        for (int i = 0; i < 24; i++) {
            final int rgb = (i * 10) + 8;
            customColors.put(NUMBER_OF_COLORS + i, new CustomColor(
                    new Color(rgb, rgb, rgb)));
        }
    }

    @Override
    public TerminalColor createStandardColor(final ColorName color,
            final boolean isBright) {
        final int colorIndex = color.ordinal();

        if (isBright) {
            final int colorCount = ColorName.values().length;
            return systemColors.get(colorIndex + colorCount);
        } else {
            return systemColors.get(colorIndex);
        }
    }

    @Override
    public TerminalColor createCustomColor(final int colorCode) {
        if (colorCode < 16) {
            final int systemColorCode = colorCode % 8 + ColorName.BLACK.ordinal();
            final boolean isBright = colorCode < 8;
            return new SystemColor(ColorName.values()[systemColorCode],
                    isBright);
        }
        return customColors.get(colorCode);
    }

    @Override
    public void updateCustomColor(final int colorCode,
            final int red, final int green, final int blue) {
        LOGGER.debug("Setting new cucstom color " + colorCode + " to "
                + red + "/" + green + "/" + blue);
        customColors.get(colorCode).color = new Color(red, green, blue);
    }
}