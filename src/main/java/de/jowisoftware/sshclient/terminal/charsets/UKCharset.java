package de.jowisoftware.sshclient.terminal.charsets;



public class UKCharset implements GfxCharset {
    @Override
    public char getUnicodeChar(final char character) {
        return (character == '#') ? '£' : character;
    }
}
