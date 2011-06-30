package de.jowisoftware.ssh.client.tty;

import de.jowisoftware.ssh.client.ui.GfxChar;

public interface GfxCharSetup<T extends GfxChar> {
    void reset();
    void setAttribute(Attribute attribute);
    void removeAttribute(Attribute attribute);
    void setForeground(Color color);
    void setBackground(Color color);

    T createChar(char character);
}
