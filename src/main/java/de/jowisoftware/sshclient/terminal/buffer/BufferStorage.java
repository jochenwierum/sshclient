package de.jowisoftware.sshclient.terminal.buffer;

public interface BufferStorage<T extends GfxChar> {
    Position size();
    void newSize(final int width, final int height);

    void shiftLines(final int offset, final int start, final int end);

    void setCharacter(final int y, final int x, final T character);
    T getCharacterAt(final int row, final int column);
    void erase(final Range range);

    T[][] cloneContent();
}