package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.ui.terminal.Snapshot;

public interface Renderer {
    void renderSnapshot(Snapshot snapshot);

    int getLines();
    int getCharsPerLine();

    void renderInverted(boolean inverted);

    Position translateMousePosition(int x, int y);

    void clearSelection();
    void setSelection(Position pos1, Position pos2);

    void setFocused(boolean isFocused);
}
