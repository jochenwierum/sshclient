package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Graphics;

public interface GfxAwtCharset {
    void drawCharacter(Graphics g, char character, int x, int y);
}
