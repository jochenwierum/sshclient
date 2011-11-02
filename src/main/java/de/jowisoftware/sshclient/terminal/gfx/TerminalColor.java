package de.jowisoftware.sshclient.terminal.gfx;

import java.awt.Color;

public interface TerminalColor {
    boolean isColor(ColorName color);
    Color getColor();
    boolean isBright();
}
