package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import de.jowisoftware.sshclient.terminal.gfx.ColorFactory;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;

public class AWTColorFactory implements ColorFactory {
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

    private class CustomColor implements TerminalColor {
        private final int colorCode;
        private final boolean isForeground;

        public CustomColor(final int colorCode, final boolean isForeground) {
            this.colorCode = colorCode;
            this.isForeground = isForeground;
        }

        @Override
        public Color getColor() {
            return customColors.get(colorCode);
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
    }

    @Override
    public TerminalColor createStandardColor(final int colorCode) {
        final ColorName color = ColorName.find(colorCode);
        if (color != null) {
            return new SystemColor(color, ColorName.isForeground(colorCode),
                    createBrightColors);
        }
        return null;
    }

    @Override
    public TerminalColor createStandardColor(final ColorName color, final boolean isForeground) {
        return new SystemColor(color, isForeground, createBrightColors);
    }

    @Override
    public TerminalColor createCustomColor(final int colorCode, final boolean isForeground) {
        if (colorCode <= 16) {
            final int systemColorCode = colorCode % 8 + ColorName.BLACK.ordinal();
            final boolean isBright = colorCode < 8;
            return new SystemColor(ColorName.values()[systemColorCode],
                    isForeground, isBright);
        }
        return new CustomColor(colorCode, isForeground);
    }

    @Override
    public void updateCustomColor(final int colorCode,
            final int red, final int green, final int blue) {
        customColors.put(colorCode, new Color(red, green, blue));
    }

    public void createBrightColors(final boolean brightColors) {
        this.createBrightColors = brightColors;
    }
}