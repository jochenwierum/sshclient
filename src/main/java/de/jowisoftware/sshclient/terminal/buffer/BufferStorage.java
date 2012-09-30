package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;


public interface BufferStorage {
    public static final GfxChar EMPTY = new GfxChar() {
        @Override public int getCharCount() { return 1; }
        @Override public String getCharAsString() { return null; }
    };

    void newSize(final int width, final int height);

    void shiftLines(final int offset, final int start, final int end);
    void shiftColumns(final int offset, final int x, final int y);

    void setCharacter(final int row, final int column, final GfxChar character);
    GfxChar getCharacterAt(final int row, final int column);

    void setClearChar(final GfxChar clearChar);
    void erase(final Range range);

    SnapshotWithHistory cloneContentWithHistory();
    public int getHistorySize();
}