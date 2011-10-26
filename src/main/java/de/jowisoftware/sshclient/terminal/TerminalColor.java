package de.jowisoftware.sshclient.terminal;

public interface TerminalColor {
    Object getColor(boolean bright);
    boolean isForeground();
    boolean isColor(ColorName color);
}
