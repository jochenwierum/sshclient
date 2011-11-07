package de.jowisoftware.sshclient.terminal.mouse;

import java.io.Serializable;

import de.jowisoftware.sshclient.terminal.buffer.Position;

public interface MouseCursorManager extends Serializable {
    void startSelection(Position charPosition);
    void updateSelectionEnd(Position charPosition);
    void copySelection();
    void copyWordUnderCursor(Position charPosition);
    void copyLineUnderCursor(Position charPosition);
}
