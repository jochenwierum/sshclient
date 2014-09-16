package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
