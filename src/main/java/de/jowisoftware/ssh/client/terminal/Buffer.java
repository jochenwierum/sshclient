package de.jowisoftware.ssh.client.terminal;

import de.jowisoftware.ssh.client.ui.GfxChar;

public interface Buffer<T extends GfxChar> {
    void newSize(int width, int height);

    void setCursorPosition(final CursorPosition position);
    void setAbsoluteCursorPosition(CursorPosition cursorPosition);
    CursorPosition getCursorPosition();
    CursorPosition getAbsoluteCursorPosition();

    T getCharacter(final int row, final int column);

    void addCharacter(final T character);
    void addNewLine();

    void eraseToBottom();
    void eraseRestOfLine();
    void eraseStartOfLine();
    void eraseFromTop();
    void erase();
    void eraseLine();

    void setRollRange(int i, int j);
    void deleteRollRange();

    void render();
}