package de.jowisoftware.sshclient.terminal.buffer;

public interface Buffer<T extends GfxChar> {
    void newSize(int width, int height);
    Position getSize();

    void setCursorRelativeToMargin(boolean b);
    void setCursorPosition(final Position position);
    Position getCursorPosition();
    void saveCursorPosition();
    void restoreCursorPosition();

    void addCharacter(final T character);
    void addNewLine();
    T getCharacter(final int row, final int column);
    void processBackspace();
    void tapstop(Tabstop vertical);

    void erase(Range range);
    void insertLines(int lines);

    void setMargin(int start, int end);
    void resetMargin();
    void moveCursorUpAndRoll();
    void moveCursorDownAndRoll(boolean resetToFirstColumn);

    void render(Renderer<T> renderer);

    void switchBuffer(BufferSelection selection);
    BufferSelection getSelectedBuffer();
}