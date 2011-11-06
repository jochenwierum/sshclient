package de.jowisoftware.sshclient.terminal.buffer;

import java.io.Serializable;


public interface Renderer extends Serializable {
    void clear();
    void renderChars(GfxChar[][] characters, Position cursorPosition);
    void swap();

    int getLines();
    int getCharsPerLine();

    void renderInverted(boolean inverted);

    Position translateMousePosition(int x, int y);

    void clearSelection();
    void setSelection(Position pos1, Position pos2);
}
