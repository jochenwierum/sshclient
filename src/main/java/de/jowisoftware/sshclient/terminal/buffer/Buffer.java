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
    void shift(int charCount);

    void setCursorRelativeToMargin(boolean b);
    void setCursorPosition(final Position position);
    Position getCursorPosition();
    void saveCursorPosition();
    void restoreCursorPosition();
    void resetMargin();
    void moveCursor();
    void moveCursorDown(boolean resetToFirstColumn);
    void setMargin(int start, int end);

    void render(Renderer renderer);
    void setShowCursor(boolean doIt);

    void switchBuffer(BufferSelection selection);
    BufferSelection getSelectedBuffer();

    void tabulator(TabulatorOrientation vertical);
}