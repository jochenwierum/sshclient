package de.jowisoftware.sshclient.terminal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TerminalCharsetTest {
    @Test
    public void testGetByIdentifier() {
        assertEquals(TerminalCharset.DECCHARS,
                TerminalCharset.getByIdentifier('0'));
        assertEquals(TerminalCharset.UK,
                TerminalCharset.getByIdentifier('A'));
        assertEquals(TerminalCharset.USASCII,
                TerminalCharset.getByIdentifier('B'));
    }

    @Test
    public void testIllegalIdentifier() {
        assertEquals(TerminalCharset.USASCII,
                TerminalCharset.getByIdentifier('?'));
    }
}
