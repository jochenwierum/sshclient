package de.jowisoftware.sshclient.terminal.mouse;

import de.jowisoftware.sshclient.terminal.buffer.Position;

public interface MouseCursorManager {
    void startSelection(Position charPosition, int clicks);
    void updateSelectionEnd(Position charPosition);
    void copySelection();
}
