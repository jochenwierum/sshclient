package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.ui.GfxChar;

public interface Buffer<T extends GfxChar> {
    void newSize(int width, int height);

    void addCharacter(final T character);
    void addNewLine();
    T getCharacter(final int row, final int column);

    void setCursorPosition(final Position position);
    void setAbsoluteCursorPosition(Position cursorPosition);
    void setSafeCursorPosition(Position offset);
    void moveCursorUpAndRoll();
    void moveCursorDownAndRoll(boolean resetToFirstColumn);
    Position getCursorPosition();
    Position getAbsoluteCursorPosition();

    void eraseToBottom();
    void eraseRestOfLine();
    void eraseStartOfLine();
    void eraseFromTop();
    void erase();
    void eraseLine();

    void setRollRange(int start, int end);
    void deleteRollRange();

    void render();
}