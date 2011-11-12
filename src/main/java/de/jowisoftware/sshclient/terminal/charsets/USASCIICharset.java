package de.jowisoftware.sshclient.terminal.charsets;

import java.util.Map;

public class USASCIICharset extends AbstractMappingCharset {
    @Override
    protected void init(final Map<Integer, Character> charmap) {
        // no character is changed
    }
}
