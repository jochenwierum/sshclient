package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.ui.GfxChar;

public interface GfxCharSetup<T extends GfxChar> {
    void reset();
    void setAttribute(Attribute attribute);
    void removeAttribute(Attribute attribute);
    void setForeground(Color color);
    void setBackground(Color color);

    T createChar(char character);
}
