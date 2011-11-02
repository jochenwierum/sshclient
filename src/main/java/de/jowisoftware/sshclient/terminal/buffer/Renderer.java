package de.jowisoftware.sshclient.terminal.buffer;

import java.util.Set;


public interface Renderer {
    void clear();
    void renderChar(GfxChar character, int x, int y, Set<RenderFlag> flags);
    void swap();

    int getLines();
    int getCharsPerLine();

    void renderInverted(boolean inverted);

    Position translateMousePosition(int x, int y);
}
