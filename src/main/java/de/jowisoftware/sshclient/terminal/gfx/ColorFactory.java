package de.jowisoftware.sshclient.terminal.gfx;

public interface ColorFactory {
    TerminalColor createStandardColor(final int colorCode);
    TerminalColor createStandardColor(final ColorName color, final boolean isForeground);
    TerminalColor getCustomColor(final int colorCode, final boolean isForeground);
}