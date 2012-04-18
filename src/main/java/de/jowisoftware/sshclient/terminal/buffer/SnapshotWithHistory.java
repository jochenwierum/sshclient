package de.jowisoftware.sshclient.terminal.buffer;

import java.util.Iterator;

import de.jowisoftware.sshclient.ui.terminal.Snapshot;
import de.jowisoftware.sshclient.util.RingBuffer;

public class SnapshotWithHistory {
    private final GfxChar[][] buffer;
    private final RingBuffer<GfxChar[]> history;
    private Position cursorPosition = null;

    public SnapshotWithHistory(final GfxChar[][] buffer, final RingBuffer<GfxChar[]> history) {
        this.buffer = buffer;
        this.history = history;
    }

    public Position getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(final Position cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public GfxChar[][] getBuffer() {
        return buffer;
    }

    public RingBuffer<GfxChar[]> getHistory() {
        return history;
    }

    public Snapshot createSimpleSnapshot(final int scrollBack) {
        if (scrollBack == 0) {
            return new Snapshot(buffer, cursorPosition);
        } else {
            final GfxChar[][] result = new GfxChar[buffer.length][];

            final int historyEnd = Math.min(history.size(), scrollBack);
            final int historyStart = Math.max(historyEnd - buffer.length, 0);
            final int contentMax = Math.max(buffer.length - historyEnd, 0);

            final Iterator<GfxChar[]> iterator = history.reversed().iterator();
            for (int i = 0; i < historyStart; ++i) {
                iterator.next();
            }

            for (int i = 0; i < historyEnd - historyStart; ++i) {
                result[historyEnd - historyStart - 1 - i] = iterator.next();
            }

            for (int i = 0; i < contentMax; ++i) {
                result[historyEnd - historyStart + i] = buffer[i];
            }

            return new Snapshot(result, cursorPosition.offset(0, historyEnd));
        }
    }
}