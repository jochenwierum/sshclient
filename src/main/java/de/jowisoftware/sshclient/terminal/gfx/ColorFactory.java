package de.jowisoftware.sshclient.terminal.gfx;

public interface ColorFactory {
    TerminalColor createStandardColor(final int colorCode);
    TerminalColor createStandardColor(final ColorName color, final boolean isForeground);
    TerminalColor createCustomColor(final int colorCode, final boolean isForeground);
    void updateCustomColor(int colorNumber, int red, int green, int blue);
}