package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public interface GfxCharSetup<T extends GfxChar> {
    void reset();
    void setAttribute(Attribute attribute);
    void removeAttribute(Attribute attribute);
    void setForeground(TerminalColor color);
    void setBackground(TerminalColor color);

    void setCharset(TerminalCharsetSelection selection, TerminalCharset charset);
    void selectCharset(TerminalCharsetSelection selection);

    void setInverseMode(boolean b);

    T createChar(char character);
    T createClearChar();
}
