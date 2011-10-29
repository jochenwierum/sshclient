package de.jowisoftware.sshclient.terminal.charsets;



public class USASCIICharset implements GfxCharset {
    @Override
    public char getUnicodeChar(final char character) {
        return character;
    }
}
