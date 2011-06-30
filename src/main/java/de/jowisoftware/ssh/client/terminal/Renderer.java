package de.jowisoftware.ssh.client.terminal;

import de.jowisoftware.ssh.client.ui.GfxChar;

public interface Renderer<T extends GfxChar> {
    void clear();
    void renderChar(T character, int x, int y);
    void swap();

    int getLines();
    int getCharsPerLine();
}
