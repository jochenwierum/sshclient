package de.jowisoftware.sshclient.terminal.gfx;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;

public interface GfxCharSetup {
    ColorFactory getColorFactory();

    void reset();
    void setAttribute(Attribute attribute);
    void removeAttribute(Attribute attribute);
    void setForeground(TerminalColor color);
    void setBackground(TerminalColor color);

    void setCharset(TerminalCharsetSelection selection, TerminalCharset charset);
    void selectCharset(TerminalCharsetSelection selection);

    void setInverseMode(boolean b);

    GfxChar createChar(char character);
    GfxChar createClearChar();
}
