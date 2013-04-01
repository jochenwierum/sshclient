package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;
import de.jowisoftware.sshclient.util.RingBuffer;

public interface SnapshotWithHistory {
    GfxChar[][] getBuffer();

    Position getCursorPosition();
    void setCursorPosition(final Position cursorPosition);

    Snapshot createSimpleSnapshot(final int scrollBack);
}
