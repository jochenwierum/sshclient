package de.jowisoftware.ssh.client.tty.controlsequences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.ssh.client.tty.Buffer;
import de.jowisoftware.ssh.client.tty.GfxCharSetup;
import de.jowisoftware.ssh.client.tty.GfxCharSetup.Attributes;
import de.jowisoftware.ssh.client.tty.GfxCharSetup.Colors;
import de.jowisoftware.ssh.client.ui.GfxChar;

@RunWith(JMock.class)
public class DisplayAttributeControlSequenceTest {
    private final Mockery context = new JUnit4Mockery();
    private DisplayAttributeControlSequence<GfxChar> seq;

    @Before
    public void setUp() {
        seq = new DisplayAttributeControlSequence<GfxChar>();
    }

    @Test
    public void testFullMatches() {
        assertTrue(seq.canHandleSequence("[1m"));
        assertTrue(seq.canHandleSequence("[12m"));
        assertTrue(seq.canHandleSequence("[12;1m"));
        assertTrue(seq.canHandleSequence("[22;7;3m"));
        assertFalse(seq.canHandleSequence("[m"));
        assertFalse(seq.canHandleSequence("[12;m"));
    }

    @Test
    public void testPartialMatches() {
        assertTrue(seq.isPartialStart("["));
        assertTrue(seq.isPartialStart("[1"));
        assertTrue(seq.isPartialStart("[12;"));
        assertTrue(seq.isPartialStart("[22;7"));
        assertTrue(seq.isPartialStart("[22;7;"));
        assertFalse(seq.isPartialStart("[x"));
        assertFalse(seq.isPartialStart("[1;x"));
        assertFalse(seq.isPartialStart("[1;2k"));
        assertFalse(seq.isPartialStart("[1;2;;"));
    }

    @SuppressWarnings("unchecked")
    private void callWithAttrAndExpect(final int attr, final Attributes expect) {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-" + attr + "-expect");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-" + attr + "-expect");

        context.checking(new Expectations() {{
            oneOf(setup).setAttribute(expect);
        }});

        seq.handleSequence("[" + attr + "m", buffer, setup);
    }

    @SuppressWarnings("unchecked")
    private void callWithAttrAndExpectFGColor(final int attr, final Colors expect) {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-" + attr + "-expect");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-" + attr + "-expect");

        context.checking(new Expectations() {{
            oneOf(setup).setForeground(expect);
        }});

        seq.handleSequence("[" + attr + "m", buffer, setup);
    }

    @SuppressWarnings("unchecked")
    private void callWithAttrAndExpectBGColor(final int attr, final Colors expect) {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-" + attr + "-expect");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-" + attr + "-expect");

        context.checking(new Expectations() {{
            oneOf(setup).setBackground(expect);
        }});

        seq.handleSequence("[" + attr + "m", buffer, setup);
    }

    @Test
    public void testAttributes() {
        callWithAttrAndExpect(1, Attributes.BRIGHT);
        callWithAttrAndExpect(2, Attributes.DIM);
        callWithAttrAndExpect(4, Attributes.UNDERSCORE);
        callWithAttrAndExpect(5, Attributes.BLINK);
        callWithAttrAndExpect(7, Attributes.REVERSE);
        callWithAttrAndExpect(8, Attributes.HIDDEN);

        callWithAttrAndExpectFGColor(30, Colors.BLACK);
        callWithAttrAndExpectFGColor(31, Colors.RED);
        callWithAttrAndExpectFGColor(32, Colors.GREEN);
        callWithAttrAndExpectFGColor(33, Colors.YELLOW);
        callWithAttrAndExpectFGColor(34, Colors.BLUE);
        callWithAttrAndExpectFGColor(35, Colors.MAGENTA);
        callWithAttrAndExpectFGColor(36, Colors.CYAN);
        callWithAttrAndExpectFGColor(37, Colors.WHITE);

        callWithAttrAndExpectBGColor(40, Colors.BLACK);
        callWithAttrAndExpectBGColor(41, Colors.RED);
        callWithAttrAndExpectBGColor(42, Colors.GREEN);
        callWithAttrAndExpectBGColor(43, Colors.YELLOW);
        callWithAttrAndExpectBGColor(44, Colors.BLUE);
        callWithAttrAndExpectBGColor(45, Colors.MAGENTA);
        callWithAttrAndExpectBGColor(46, Colors.CYAN);
        callWithAttrAndExpectBGColor(47, Colors.WHITE);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testResetAttributes() {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-0-expect");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-0-expect");

        context.checking(new Expectations() {{
            oneOf(setup).reset();
        }});

        seq.handleSequence("[0m", buffer, setup);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultipleAttributes() {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class);
        final Buffer<GfxChar> buffer = context.mock(Buffer.class);

        context.checking(new Expectations() {{
            oneOf(setup).setBackground(Colors.RED);
            oneOf(setup).setForeground(Colors.BLUE);
            oneOf(setup).setAttribute(Attributes.BLINK);
        }});

        seq.handleSequence("[5;34;41m", buffer, setup);
    }
}
