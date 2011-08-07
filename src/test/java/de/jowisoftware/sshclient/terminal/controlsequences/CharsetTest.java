package de.jowisoftware.sshclient.terminal.controlsequences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.TerminalCharset;
import de.jowisoftware.sshclient.terminal.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

@RunWith(JMock.class)
public class CharsetTest extends AbstractSequenceTest {
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

        final CharsetControlSequence<GfxChar> seq =
            new CharsetControlSequence<GfxChar>();
        seq.handleSequence(input, sessionInfo);
    }

    @Test
    public void testCanHandle() {
        final CharsetControlSequence<GfxChar> seq =
            new CharsetControlSequence<GfxChar>();

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
        final CharsetControlSequence<GfxChar> seq = new CharsetControlSequence<GfxChar>();

        assertTrue(seq.isPartialStart("("));
        assertTrue(seq.isPartialStart(")"));
        assertFalse(seq.isPartialStart("X"));
    }

    @Test
    public void testSelectCharsetG0() {
        context.checking(new Expectations() {{
            oneOf(charSetup).selectCharset(TerminalCharsetSelection.G0);
        }});

        final CharsetControlSequence<GfxChar> seq =
            new CharsetControlSequence<GfxChar>();
        seq.handleSequence("O", sessionInfo);
    }

    @Test
    public void testSelectCharsetG1() {
        context.checking(new Expectations() {{
            oneOf(charSetup).selectCharset(TerminalCharsetSelection.G1);
        }});

        final CharsetControlSequence<GfxChar> seq =
            new CharsetControlSequence<GfxChar>();
        seq.handleSequence("N", sessionInfo);
    }
}
