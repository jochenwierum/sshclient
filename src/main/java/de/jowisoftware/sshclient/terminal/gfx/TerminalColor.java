package de.jowisoftware.sshclient.terminal.gfx;

public interface TerminalColor {
    boolean isColor(ColorName color);
    boolean isBright();
    TerminalColor invert();
    Object getColor();
}
