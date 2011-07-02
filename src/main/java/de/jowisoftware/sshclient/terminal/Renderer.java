package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.ui.GfxChar;

public interface Renderer<T extends GfxChar> {
    void clear();
    void renderChar(T character, int x, int y, boolean isCursor);
    void swap();

    int getLines();
    int getCharsPerLine();
}
