package de.jowisoftware.sshclient.terminal.charsets;

import java.util.Map;

public class UKCharset extends AbstractMappingCharset {
    @Override
    protected void init(final Map<Integer, Character> charmap) {
        charmap.put(0x23, '£'); // #
    }
}
