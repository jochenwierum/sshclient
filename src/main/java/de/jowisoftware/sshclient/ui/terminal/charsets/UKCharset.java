package de.jowisoftware.sshclient.ui.terminal.charsets;

import de.jowisoftware.sshclient.ui.terminal.GfxAwtCharset;

public class UKCharset implements GfxAwtCharset {
    @Override
    public char getUnicodeChar(final char character) {
        return (character == '#') ? '£' : character;
    }
}
