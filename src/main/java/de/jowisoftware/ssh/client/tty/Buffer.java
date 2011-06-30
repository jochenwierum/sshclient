package de.jowisoftware.ssh.client.tty;

import de.jowisoftware.ssh.client.ui.GfxChar;

public interface Buffer<T extends GfxChar> {

    public int rows();

    public void setCursorPosition(final CursorPosition position);

    public CursorPosition getCursorPosition();

    public T getCharacter(final int column, final int row);

    public int lengthOfLine(final int row);

    public void addNewLine();

    public void addCharacter(final T character);

    public void eraseToBottom();
    public void eraseRestOfLine();
    public void eraseStartOfLine();
    public void eraseFromTop();
    public void erase();
    public void eraseLine();
}