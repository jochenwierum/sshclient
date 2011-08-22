package de.jowisoftware.sshclient.terminal.buffer;

import java.util.Set;


public interface Renderer<T extends GfxChar> {
    void clear();
    void renderChar(T character, int x, int y, Set<RenderFlag> flags);
    void swap();

    int getLines();
    int getCharsPerLine();

    Position translateMousePosition(int x, int y);
}
