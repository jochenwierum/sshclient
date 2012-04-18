package de.jowisoftware.sshclient.terminal.buffer;

public interface Buffer {
    void newSize(int width, int height);
    Position getSize();

    void addCharacter(final GfxChar character);
    GfxChar getCharacter(final int row, final int column);
    void processBackspace();
    void setAutoWrap(boolean autoWrap);

    void setClearChar(GfxChar clearChar);
    void erase(Range range);
    void insertLines(int lines);
    void removeLines(int lines);
    void shift(int charCount);

    void setCursorRelativeToMargin(boolean b);
    void setCursorPosition(final Position position);
    Position getCursorPosition();
    void saveCursorPosition();
    void restoreCursorPosition();
    void resetMargin();
    void moveCursorUp();
    void moveCursorDown(boolean resetToFirstColumn);
    void setMargin(int start, int end);

    void setShowCursor(boolean doIt);
    SnapshotWithHistory createSnapshot();
    int getHistorySize();

    void switchBuffer(BufferSelection selection);
    BufferSelection getSelectedBuffer();

    void tabulator(TabulatorOrientation vertical);
}