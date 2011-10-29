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

        public SystemColor(final ColorName colorName, final boolean isForeground) {
            this.colorName = colorName;
            this.isForeground = isForeground;
        }

        @Override
        public Color getColor(final boolean bright) {
            return gfxInfo.mapColor(colorName, bright);
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
        public Color getColor(final boolean bright) {
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

    public AWTColorFactory(final AWTGfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        this.customColors = new HashMap<Integer, Color>();
    }

    @Override
    public TerminalColor createStandardColor(final int colorCode) {
        final ColorName color = ColorName.find(colorCode);
        if (color != null) {
            return new SystemColor(color, ColorName.isForeground(colorCode));
        }
        return null;
    }

    @Override
    public TerminalColor createStandardColor(final ColorName color, final boolean isForeground) {
        return new SystemColor(color, isForeground);
    }

    @Override
    public TerminalColor getCustomColor(final int colorCode, final boolean isForeground) {
        return new CustomColor(colorCode, isForeground);
    }

    @Override
    public void updateCustomColor(final int colorCode,
            final int red, final int green, final int blue) {
        customColors.put(colorCode, new Color(red, green, blue));
    }
}