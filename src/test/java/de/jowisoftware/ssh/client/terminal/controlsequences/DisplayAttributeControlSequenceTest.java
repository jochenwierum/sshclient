package de.jowisoftware.ssh.client.terminal.controlsequences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.ssh.client.terminal.Attribute;
import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.terminal.Color;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;
import de.jowisoftware.ssh.client.terminal.controlsequences.DisplayAttributeControlSequence;
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
        assertTrue(seq.canHandleSequence("[m"));
        assertTrue(seq.canHandleSequence("[1m"));
        assertTrue(seq.canHandleSequence("[12m"));
        assertTrue(seq.canHandleSequence("[12;1m"));
        assertTrue(seq.canHandleSequence("[22;7;3m"));
        assertFalse(seq.canHandleSequence("[y"));
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
    private void callWithAttrAndExpect(final int attr, final Attribute expect) {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-" + attr + "-expect");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-" + attr + "-expect");

        context.checking(new Expectations() {{
            oneOf(setup).setAttribute(expect);
        }});

        seq.handleSequence("[" + attr + "m", buffer, setup);
    }

    @SuppressWarnings("unchecked")
    private void callWithAttrAndExpectFGColor(final int attr, final Color expect) {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-" + attr + "-expect");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-" + attr + "-expect");

        context.checking(new Expectations() {{
            oneOf(setup).setForeground(expect);
        }});

        seq.handleSequence("[" + attr + "m", buffer, setup);
    }

    @SuppressWarnings("unchecked")
    private void callWithAttrAndExpectBGColor(final int attr, final Color expect) {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-" + attr + "-expect");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-" + attr + "-expect");

        context.checking(new Expectations() {{
            oneOf(setup).setBackground(expect);
        }});

        seq.handleSequence("[" + attr + "m", buffer, setup);
    }

    @SuppressWarnings("unchecked")
    private void callWithRemoveAttrAndExpect(final int attr, final Attribute expect) {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-" + attr + "-remove");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-" + attr + "-remove");

        context.checking(new Expectations() {{
            oneOf(setup).removeAttribute(expect);
        }});

        seq.handleSequence("[" + attr + "m", buffer, setup);
    }

    @Test
    public void testAttributes() {
        callWithAttrAndExpect(1, Attribute.BRIGHT);
        callWithAttrAndExpect(2, Attribute.DIM);
        callWithAttrAndExpect(4, Attribute.UNDERSCORE);
        callWithAttrAndExpect(5, Attribute.BLINK);
        callWithAttrAndExpect(7, Attribute.INVERSE);
        callWithAttrAndExpect(8, Attribute.HIDDEN);

        callWithRemoveAttrAndExpect(22, Attribute.BRIGHT);
        callWithRemoveAttrAndExpect(23, Attribute.DIM); // is this correct?
        callWithRemoveAttrAndExpect(24, Attribute.UNDERSCORE);
        callWithRemoveAttrAndExpect(25, Attribute.BLINK);
        callWithRemoveAttrAndExpect(27, Attribute.INVERSE);
        callWithRemoveAttrAndExpect(28, Attribute.HIDDEN);

        callWithAttrAndExpectFGColor(30, Color.BLACK);
        callWithAttrAndExpectFGColor(31, Color.RED);
        callWithAttrAndExpectFGColor(32, Color.GREEN);
        callWithAttrAndExpectFGColor(33, Color.YELLOW);
        callWithAttrAndExpectFGColor(34, Color.BLUE);
        callWithAttrAndExpectFGColor(35, Color.MAGENTA);
        callWithAttrAndExpectFGColor(36, Color.CYAN);
        callWithAttrAndExpectFGColor(37, Color.WHITE);

        callWithAttrAndExpectBGColor(40, Color.BLACK);
        callWithAttrAndExpectBGColor(41, Color.RED);
        callWithAttrAndExpectBGColor(42, Color.GREEN);
        callWithAttrAndExpectBGColor(43, Color.YELLOW);
        callWithAttrAndExpectBGColor(44, Color.BLUE);
        callWithAttrAndExpectBGColor(45, Color.MAGENTA);
        callWithAttrAndExpectBGColor(46, Color.CYAN);
        callWithAttrAndExpectBGColor(47, Color.WHITE);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testResetAttributes() {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class, "setup-0-expect");
        final Buffer<GfxChar> buffer = context.mock(Buffer.class, "buffer-0-expect");

        context.checking(new Expectations() {{
            oneOf(setup).reset();
            oneOf(setup).reset();
        }});

        seq.handleSequence("[0m", buffer, setup);
        seq.handleSequence("[m", buffer, setup);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultipleAttributes() {
        final GfxCharSetup<GfxChar> setup = context.mock(GfxCharSetup.class);
        final Buffer<GfxChar> buffer = context.mock(Buffer.class);

        context.checking(new Expectations() {{
            oneOf(setup).setBackground(Color.RED);
            oneOf(setup).setForeground(Color.BLUE);
            oneOf(setup).setAttribute(Attribute.BLINK);
        }});

        seq.handleSequence("[5;34;41m", buffer, setup);
    }
}
