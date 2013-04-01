package de.jowisoftware.sshclient.terminal.gfx;


public interface TerminalColor {
    TerminalColor invert();
    ColorName name();
    Object getColor();
}
