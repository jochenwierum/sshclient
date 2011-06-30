package de.jowisoftware.ssh.client.terminal;

import de.jowisoftware.ssh.client.ui.GfxChar;

public interface Buffer<T extends GfxChar> {
    public void setCursorPosition(final CursorPosition position);
    public CursorPosition getCursorPosition();

    public int rows();
    public int lengthOfLine(final int row);

    public T getCharacter(final int column, final int row);

    public void addCharacter(final T character);
    public void addNewLine();

    public void eraseToBottom();
    public void eraseRestOfLine();
    public void eraseStartOfLine();
    public void eraseFromTop();
    public void erase();
    public void eraseLine();

    void render();
}