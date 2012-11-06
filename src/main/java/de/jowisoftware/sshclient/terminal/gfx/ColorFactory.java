package de.jowisoftware.sshclient.terminal.gfx;


public interface ColorFactory {
    TerminalColor createStandardColor(ColorName color, boolean isForeground);
    TerminalColor createCustomColor(final int colorCode);
    void updateCustomColor(int colorNumber, int red, int green, int blue);
}