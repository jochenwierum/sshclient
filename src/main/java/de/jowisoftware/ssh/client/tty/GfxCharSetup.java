package de.jowisoftware.ssh.client.tty;

import de.jowisoftware.ssh.client.ui.GfxChar;

public interface GfxCharSetup<T extends GfxChar> {
    enum Attributes {
        BRIGHT, DIM, BLINK, UNDERSCORE, REVERSE, HIDDEN;
    }

    enum Colors {
        DEFAULT, DEFAULTBG, BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE
    }

    void reset();
    void setAttribute(Attributes attribute);
    void setForeground(Colors color);
    void setBackground(Colors color);

    T createChar(char character);
}
