package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
