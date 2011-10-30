package de.jowisoftware.sshclient.terminal.buffer;


public interface Buffer {
    void newSize(int width, int height);
    Position getSize();

    void setCursorRelativeToMargin(boolean b);
    void setCursorPosition(final Position position);
    Position getCursorPosition();
    void saveCursorPosition();
    void restoreCursorPosition();

    void addCharacter(final GfxChar character);
    void addNewLine();
    GfxChar getCharacter(final int row, final int column);
    void processBackspace();
    void tabulator(TabulatorOrientation vertical);
    void setAutoWrap(boolean autoWrap);

    void setClearChar(GfxChar clearChar);
    void erase(Range range);
    void insertLines(int lines);
    void shift(int charCount);

    void setMargin(int start, int end);
    void resetMargin();
    void moveCursorUpAndRoll();
    void moveCursorDownAndRoll(boolean resetToFirstColumn);

    void render(Renderer renderer);
    void setShowCursor(boolean doIt);

    void switchBuffer(BufferSelection selection);
    BufferSelection getSelectedBuffer();
}