package de.jowisoftware.sshclient.terminal.charsets;

import de.jowisoftware.sshclient.terminal.GfxCharset;


public class UKCharset implements GfxCharset {
    @Override
    public char getUnicodeChar(final char character) {
        return (character == '#') ? '£' : character;
    }
}
