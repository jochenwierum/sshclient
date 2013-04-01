package de.jowisoftware.sshclient.terminal.gfx.awt;

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
    public TerminalColor invert() {
        return this;
    }

    @Override
    public ColorName name() {
        return null;
    }
}
