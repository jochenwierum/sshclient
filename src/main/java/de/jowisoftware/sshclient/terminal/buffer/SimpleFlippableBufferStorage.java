package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.util.EmptyRingBuffer;
import de.jowisoftware.sshclient.util.FixedSizeArrayRingBuffer;
import de.jowisoftware.sshclient.util.RingBuffer;

public class SimpleFlippableBufferStorage implements FlippableBufferStorage {
    private final BufferStorage primaryBuffer;
    private final BufferStorage secondaryBuffer;
    private BufferStorage selectedBuffer;
    private final RingBuffer<GfxChar[]> history;

    public SimpleFlippableBufferStorage(final BufferStorage primary,
            final BufferStorage secondary,
            final RingBuffer<GfxChar[]> history) {
        this.primaryBuffer = primary;
        this.secondaryBuffer = secondary;
        this.selectedBuffer = primary;
        this.history = history;
    }

    public static SimpleFlippableBufferStorage create(
            final GfxChar initialClearChar, final int width, final int height,
            final int historySize) {

        final RingBuffer<GfxChar[]> history = new FixedSizeArrayRingBuffer<GfxChar[]>(
                historySize);

        final ArrayBackedBufferStorage primary =
                new ArrayBackedBufferStorage(initialClearChar, width, height, history);
        final ArrayBackedBufferStorage secondary =
                new ArrayBackedBufferStorage(initialClearChar, width, height, new EmptyRingBuffer<GfxChar[]>());
        return new SimpleFlippableBufferStorage(primary, secondary, history);
    }

    @Override
    public void flipTo(final BufferSelection selection) {
        if (selection == BufferSelection.ALTERNATIVE) {
            selectedBuffer = secondaryBuffer;
        } else {
            selectedBuffer = primaryBuffer;
        }

    }

    @Override
    public BufferSelection getSelectedStorage() {
        if (selectedBuffer == primaryBuffer) {
            return BufferSelection.PRIMARY;
        } else {
            return BufferSelection.ALTERNATIVE;
        }
    }

    @Override
    public void newSize(final int width, final int height) {
        primaryBuffer.newSize(width, height);
        secondaryBuffer.newSize(width, height);
    }

    @Override
    public void shiftLines(final int offset, final int start, final int end) {
        selectedBuffer.shiftLines(offset, start, end);
    }

    @Override
    public void shiftColumns(final int offset, final int x, final int y) {
        selectedBuffer.shiftColumns(offset, x, y);
    }

    @Override
    public void setCharacter(final int row, final int column, final GfxChar character) {
        selectedBuffer.setCharacter(row, column, character);
    }

    @Override
    public GfxChar getCharacterAt(final int row, final int column) {
        return selectedBuffer.getCharacterAt(row, column);
    }

    @Override
    public void setClearChar(final GfxChar clearChar) {
        selectedBuffer.setClearChar(clearChar);
    }

    @Override
    public void erase(final Range range) {
        selectedBuffer.erase(range);
    }

    @Override
    public SnapshotWithHistory cloneContentWithHistory() {
        final SnapshotWithHistory snapshot = selectedBuffer.cloneContentWithHistory();
        final SnapshotWithHistory result = new SnapshotWithHistory(snapshot.getBuffer(), history);
        result.setCursorPosition(snapshot.getCursorPosition());
        return result;
    }

    @Override
    public int getHistorySize() {
        return primaryBuffer.getHistorySize();
    }
}
