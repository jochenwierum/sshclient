package de.jowisoftware.sshclient.terminal.buffer;

public class SimpleFlippableBufferStorage implements FlippableBufferStorage {
    BufferStorage primaryBuffer;
    BufferStorage secondaryBuffer;
    BufferStorage selectedBuffer;

    public SimpleFlippableBufferStorage(final BufferStorage primary,
            final BufferStorage secondary) {
        this.primaryBuffer = primary;
        this.secondaryBuffer = secondary;
        this.selectedBuffer = primary;
    }

    public static SimpleFlippableBufferStorage create(
            final GfxChar initialClearChar, final int width, final int height) {
        final ArrayBackedBufferStorage primary =
                new ArrayBackedBufferStorage(initialClearChar, width, height);
        final ArrayBackedBufferStorage secondary =
                new ArrayBackedBufferStorage(initialClearChar, width, height);
        return new SimpleFlippableBufferStorage(primary, secondary);
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
    public Position size() {
        return selectedBuffer.size();
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
    public GfxChar[][] cloneContent() {
        return selectedBuffer.cloneContent();
    }
}
