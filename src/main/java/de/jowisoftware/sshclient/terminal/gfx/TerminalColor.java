package de.jowisoftware.sshclient.terminal.gfx;

import java.awt.Color;

public interface TerminalColor {
    boolean isForeground();
    boolean isColor(ColorName color);
    Color getColor();
}
