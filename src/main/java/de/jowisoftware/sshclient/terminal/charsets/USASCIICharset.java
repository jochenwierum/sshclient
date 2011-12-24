package de.jowisoftware.sshclient.terminal.charsets;

import java.util.Map;

public class USASCIICharset extends AbstractMappingCharset {
    private static final USASCIICharset instance = new USASCIICharset();
    private USASCIICharset() {}

    @Override
    protected void init(final Map<Integer, Character> charmap) {
        // no character is changed
    }

    public static GfxCharset instance() {
        return instance;
    }
}
