package de.jowisoftware.sshclient.terminal.charsets;

import java.util.HashMap;
import java.util.Map;

import de.jowisoftware.sshclient.terminal.GfxCharset;


public class DECCharset implements GfxCharset {
    private static final Map<Integer, Character> charMap
            = new HashMap<Integer, Character>();

    static {
        charMap.put(95, ' ');
        charMap.put(96, '♦');
        charMap.put(97, '░');
        charMap.put(98, ' '); // HT
        charMap.put(99, ' '); // form feed
        charMap.put(100, ' '); // caridge return
        charMap.put(101, ' '); // line feed
        charMap.put(102, '°');
        charMap.put(103, '±');
        charMap.put(104, ' '); // new line
        charMap.put(105, ' '); // vertical tab
        charMap.put(106, '┘');
        charMap.put(107, '┐');
        charMap.put(108, '┌');
        charMap.put(109, '└');
        charMap.put(110, '┼');
        charMap.put(111, '─');
        charMap.put(112, '─');
        charMap.put(113, '─');
        charMap.put(114, '─');
        charMap.put(115, '─');
        charMap.put(116, '├');
        charMap.put(117, '┤');
        charMap.put(118, '┴');
        charMap.put(119, '┬');
        charMap.put(120, '│');
        charMap.put(121, '≤');
        charMap.put(122, '≥');
        charMap.put(123, 'π');
        charMap.put(124, '≠');
        charMap.put(125, '£');
        charMap.put(125, '∙');
    }

    @Override
    public char getUnicodeChar(final char character) {
        final int codePoint = Character.codePointAt(new char[] {character}, 0);
        final Character newChar = charMap.get(codePoint);
        return (newChar != null) ? newChar : character;
    }
}
