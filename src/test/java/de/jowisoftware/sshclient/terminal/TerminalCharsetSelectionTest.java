package de.jowisoftware.sshclient.terminal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TerminalCharsetSelectionTest {
    @Test
    public void testGetByIdentifier() {
        assertEquals(TerminalCharsetSelection.G0,
                TerminalCharsetSelection.getByIdentifier('('));
        assertEquals(TerminalCharsetSelection.G1,
                TerminalCharsetSelection.getByIdentifier(')'));
    }

    @Test
    public void testIllegalIdentifier() {
        assertEquals(TerminalCharsetSelection.G0,
                TerminalCharsetSelection.getByIdentifier('?'));
    }
}
