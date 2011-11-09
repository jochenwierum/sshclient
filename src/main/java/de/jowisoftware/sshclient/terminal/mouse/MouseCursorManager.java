package de.jowisoftware.sshclient.terminal.mouse;

import de.jowisoftware.sshclient.terminal.buffer.Position;

public interface MouseCursorManager {
    void startSelection(Position charPosition);
    void updateSelectionEnd(Position charPosition);
    void copySelection();
    void copyWordUnderCursor(Position charPosition);
    void copyLineUnderCursor(Position charPosition);
}
