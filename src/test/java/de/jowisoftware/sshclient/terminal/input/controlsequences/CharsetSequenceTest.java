package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.junit.Test;

import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;

public class CharsetSequenceTest extends AbstractSequenceTest {
    @Test
    public void testSetASCIIEncoding() {
        checkCharsetSelection("(B", TerminalCharsetSelection.G0, TerminalCharset.USASCII);
    }

    @Test
    public void testSetUKEncoding() {
        checkCharsetSelection("(A", TerminalCharsetSelection.G0, TerminalCharset.UK);
    }

    @Test
    public void testSetDECEncoding() {
        checkCharsetSelection("(0", TerminalCharsetSelection.G0, TerminalCharset.DECCHARS);
    }

    @Test
    public void testSetASCIIEncodingG1() {
        checkCharsetSelection(")B", TerminalCharsetSelection.G1, TerminalCharset.USASCII);
    }

    @Test
    public void testSetUKEncodingG1() {
        checkCharsetSelection(")A", TerminalCharsetSelection.G1, TerminalCharset.UK);
    }

    @Test
    public void testSetDECEncodingG1() {
        checkCharsetSelection(")0", TerminalCharsetSelection.G1, TerminalCharset.DECCHARS);
    }

    private void checkCharsetSelection(final String input,
            final TerminalCharsetSelection selection, final TerminalCharset charset) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setCharset(selection, charset);
        }});

        final CharsetControlSequence seq =
            new CharsetControlSequence();
        seq.handleSequence(input, sessionInfo);
    }

    @Test
    public void testCanHandle() {
        final CharsetControlSequence seq =
            new CharsetControlSequence();

        assertTrue(seq.canHandleSequence("(0"));
        assertTrue(seq.canHandleSequence("(A"));
        assertTrue(seq.canHandleSequence("(B"));
        assertTrue(seq.canHandleSequence(")0"));
        assertTrue(seq.canHandleSequence(")A"));
        assertTrue(seq.canHandleSequence(")B"));
        assertFalse(seq.canHandleSequence("(X"));
        assertFalse(seq.canHandleSequence("("));
        assertFalse(seq.canHandleSequence(")X"));
        assertFalse(seq.canHandleSequence(")"));
        assertFalse(seq.canHandleSequence("A"));

        assertTrue(seq.canHandleSequence("N"));
        assertTrue(seq.canHandleSequence("O"));
    }

    @Test
    public void testCanHandlePartial() {
        final CharsetControlSequence seq = new CharsetControlSequence();

        assertTrue(seq.isPartialStart("("));
        assertTrue(seq.isPartialStart(")"));
        assertFalse(seq.isPartialStart("X"));
    }

    @Test
    public void testSelectCharsetG0() {
        context.checking(new Expectations() {{
            oneOf(charSetup).selectCharset(TerminalCharsetSelection.G0);
        }});

        final CharsetControlSequence seq =
            new CharsetControlSequence();
        seq.handleSequence("O", sessionInfo);
    }

    @Test
    public void testSelectCharsetG1() {
        context.checking(new Expectations() {{
            oneOf(charSetup).selectCharset(TerminalCharsetSelection.G1);
        }});

        final CharsetControlSequence seq =
            new CharsetControlSequence();
        seq.handleSequence("N", sessionInfo);
    }
}
