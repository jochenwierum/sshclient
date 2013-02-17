package de.jowisoftware.sshclient.terminal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;

public class TerminalCharsetSelectionTest {
    @Test
    public void testGetByIdentifier() {
        assertThat(TerminalCharsetSelection.getByIdentifier('('),
                is(TerminalCharsetSelection.G0));
        assertThat(TerminalCharsetSelection.getByIdentifier(')'),
                is(TerminalCharsetSelection.G1));
    }

    @Test
    public void testIllegalIdentifier() {
        assertThat(TerminalCharsetSelection.getByIdentifier('?'),
                is(TerminalCharsetSelection.G0));
    }
}
