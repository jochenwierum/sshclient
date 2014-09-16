package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;

public interface SnapshotWithHistory {
    GfxChar[][] getBuffer();

    Position getCursorPosition();
    void setCursorPosition(final Position cursorPosition);

    Snapshot createSimpleSnapshot(final int scrollBack);
}
