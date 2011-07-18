package de.jowisoftware.sshclient.terminal.buffer;


public interface Buffer<T extends GfxChar> {
    void newSize(int width, int height);
    Position getSize();

    void addCharacter(final T character);
    void addNewLine();
    T getCharacter(final int row, final int column);

    void erase(Range range);

    void setCursorRelativeToMargin(boolean b);
    void setCursorPosition(final Position position);
    Position getCursorPosition();

    void setMargin(int start, int end);
    void resetMargin();
    void moveCursorUpAndRoll();
    void moveCursorDownAndRoll(boolean resetToFirstColumn);
    void insertLines(int lines);

    void render(Renderer<T> renderer);
    void tapstop(Tabstop vertical);
}