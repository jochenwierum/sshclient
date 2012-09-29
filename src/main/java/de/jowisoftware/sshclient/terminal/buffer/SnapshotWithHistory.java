package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.ui.terminal.Snapshot;
import de.jowisoftware.sshclient.util.RingBuffer;

public interface SnapshotWithHistory {

    Position getCursorPosition();

    void setCursorPosition(final Position cursorPosition);

    GfxChar[][] getBuffer();

    RingBuffer<GfxChar[]> getHistory();

    Snapshot createSimpleSnapshot(final int scrollBack);

}