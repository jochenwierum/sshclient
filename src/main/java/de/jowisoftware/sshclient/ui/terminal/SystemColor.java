package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;

import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;

public class SystemColor implements TerminalColor {
    private final ColorName colorName;
    private final boolean isBright;
    private final AWTGfxInfo gfxInfo;
    public SystemColor inverseColor = this;

    public SystemColor(final ColorName colorName,
            final boolean isBright, final AWTGfxInfo gfxInfo) {
        this.colorName = colorName;
        this.isBright = isBright;
        this.gfxInfo = gfxInfo;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((colorName == null) ? 0 : colorName.hashCode());
        result = prime * result + (isBright ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final SystemColor other = (SystemColor) obj;
        return colorName == other.colorName && isBright != other.isBright;
    }
}