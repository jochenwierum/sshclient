package de.jowisoftware.sshclient.ui.terminal.charsets;

import java.awt.Graphics;

import de.jowisoftware.sshclient.ui.terminal.GfxAwtCharset;

public class USASCIICharset implements GfxAwtCharset {
    @Override
    public void drawCharacter(final Graphics g, final char character,
            final int x, final int y) {
        g.drawString(Character.toString(character), x, y);
    }
}
