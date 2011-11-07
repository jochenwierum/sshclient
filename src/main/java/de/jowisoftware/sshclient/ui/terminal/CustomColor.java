package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;

import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;

public class CustomColor implements TerminalColor {
    public Color color;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
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

        final CustomColor other = (CustomColor) obj;
        if (color == null) {
            return other.color != null;
        } else {
            return color.equals(other.color);
        }
    }
}