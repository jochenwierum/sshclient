package de.jowisoftware.sshclient.terminal.buffer;

public interface Renderer {
    void renderSnapshot(Snapshot snapshot);

    int getLines();
    int getCharsPerLine();

    void renderInverted(boolean inverted);

    void setDimensions(int pw, int ph);
    Position translateMousePosition(int x, int y);

    void clearSelection();
    void setSelection(Position pos1, Position pos2);

    void setFocused(boolean isFocused);
    void invertFor(int invertMillis);

    void resetBlinking();

    void dispose();
}
