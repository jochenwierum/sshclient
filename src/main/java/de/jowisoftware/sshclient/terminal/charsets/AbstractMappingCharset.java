package de.jowisoftware.sshclient.terminal.charsets;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractMappingCharset implements GfxCharset {
    private final Map<Integer, Character> charMap = new HashMap<>();

    protected AbstractMappingCharset() {
        init(charMap);
    }

    protected abstract void init(Map<Integer, Character> charmap);

    @Override
    public char convertCharacter(final char character) {
        final int codePoint = Character.codePointAt(new char[]{character}, 0);
        final Character newChar = charMap.get(codePoint);
        return (newChar != null) ? newChar : character;
    }
}
