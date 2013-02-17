package de.jowisoftware.sshclient.terminal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;

public class TerminalCharsetTest {
    @Test
    public void testGetByIdentifier() {
        assertThat(TerminalCharset.getByIdentifier('0'),
                is(TerminalCharset.DECCHARS));
        assertThat(TerminalCharset.getByIdentifier('A'),
                is(TerminalCharset.UK));
        assertThat(TerminalCharset.getByIdentifier('B'),
                is(TerminalCharset.USASCII));
    }

    @Test
    public void testIllegalIdentifier() {
        assertThat(TerminalCharset.getByIdentifier('?'),
                is(TerminalCharset.USASCII));
    }
}
