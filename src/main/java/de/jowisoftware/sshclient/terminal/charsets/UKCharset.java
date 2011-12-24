package de.jowisoftware.sshclient.terminal.charsets;

import java.util.Map;

public class UKCharset extends AbstractMappingCharset {
    private static final UKCharset instance = new UKCharset();
    private UKCharset() {}

    @Override
    protected void init(final Map<Integer, Character> charmap) {
        charmap.put(0x23, '£'); // #
    }

    public static GfxCharset instance() {
        return instance;
    }
}
