package de.jowisoftware.sshclient.terminal.gfx;

import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;

public interface GfxCharSetup {
    void reset();

    void setAttribute(Attribute attribute);
    void removeAttribute(Attribute attribute);

    void setForeground(ColorName color);
    void setForeground(int colorCode);
    void setBackground(ColorName color);
    void setBackground(int colorCode);
    void updateCustomColor(int colorNumber, int r, int g, int b);

    void setCharset(TerminalCharsetSelection selection, TerminalCharset charset);
    void selectCharset(TerminalCharsetSelection selection);

    GfxChar createChar(char character);
    GfxChar createMultibyteChar(String composedCharacter);
    GfxChar createClearChar();

    void save();
    void restore();
}
