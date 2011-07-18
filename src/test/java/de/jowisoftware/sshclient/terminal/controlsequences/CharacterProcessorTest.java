package de.jowisoftware.sshclient.terminal.controlsequences;

import java.nio.charset.Charset;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.CharacterProcessor;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.VisualFeedback;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.test.matches.StringBuilderEquals;

@RunWith(JMock.class)
public class CharacterProcessorTest {
    private final Mockery context = new JUnit4Mockery();
    private Buffer<GfxChar> buffer;
    private GfxCharSetup<GfxChar> setup;
    private NonASCIIControlSequence<GfxChar> sequence1;
    private NonASCIIControlSequence<GfxChar> sequence2;
    private GfxChar gfxChar;
    private CharacterProcessor<GfxChar> processor;
    private VisualFeedback visualFeedback;
    private Session<GfxChar> sessionInfo;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        buffer = context.mock(Buffer.class);
        setup = context.mock(GfxCharSetup.class);
        visualFeedback = context.mock(VisualFeedback.class);
        sequence1 = context.mock(NonASCIIControlSequence.class, "sequence1");
        sequence2 = context.mock(NonASCIIControlSequence.class, "sequence2");
        gfxChar = context.mock(GfxChar.class);
        sessionInfo = context.mock(Session.class);

        processor = new CharacterProcessor<GfxChar>(
                sessionInfo,
                Charset.defaultCharset());
        processor.addControlSequence(sequence1);
        processor.addControlSequence(sequence2);

        context.checking(new Expectations() {{
            allowing(sessionInfo).getBuffer(); will(returnValue(buffer));
            allowing(sessionInfo).getCharSetup(); will(returnValue(setup));
            allowing(sessionInfo).getVisualFeedback(); will(returnValue(visualFeedback));
        }});
    }

    @Test
    public void testDefaultChars() {
        final Sequence seq = context.sequence("seq");

        context.checking(new Expectations() {{
            expectChar('x');
            expectChar('y');
            expectChar('z');
        }

        private void expectChar(final char x) {
            oneOf(setup).createChar(x);
                inSequence(seq);
                will(returnValue(gfxChar));
            oneOf(buffer).addCharacter(gfxChar);
                inSequence(seq);
        }});

        processor.processByte((byte) Character.codePointAt("x", 0));
        processor.processByte((byte) Character.codePointAt("y", 0));
        processor.processByte((byte) Character.codePointAt("z", 0));
    }

    @Test
    public void testCharsWithNewLine() {
        final Sequence seq = context.sequence("seq");

        context.checking(new Expectations() {{
            expectChar('x');
            oneOf(buffer).addNewLine(); inSequence(seq);
            expectChar('z');
        }

        private void expectChar(final char x) {
            oneOf(setup).createChar(x);
                inSequence(seq);
                will(returnValue(gfxChar));
            oneOf(buffer).addCharacter(gfxChar);
                inSequence(seq);
        }});

        processor.processByte((byte) Character.codePointAt("x", 0));
        processor.processByte((byte) Character.codePointAt("\n", 0));
        processor.processByte((byte) Character.codePointAt("z", 0));
    }

    @Test
    public void testSequence() {
        context.checking(new Expectations() {{
            oneOf(sequence1).canHandleSequence(with(new StringBuilderEquals("t")));
                will(returnValue(false));
            oneOf(sequence2).canHandleSequence(with(new StringBuilderEquals("t")));
                will(returnValue(false));
            oneOf(sequence1).isPartialStart(with(new StringBuilderEquals("t")));
                will(returnValue(true));
            oneOf(sequence2).isPartialStart(with(new StringBuilderEquals("t")));
                will(returnValue(false));
            oneOf(sequence1).canHandleSequence(with(new StringBuilderEquals("ts")));
                will(returnValue(true));
            oneOf(sequence1).handleSequence("ts", sessionInfo);
            oneOf(setup).createChar('3'); will(returnValue(gfxChar));
            oneOf(buffer).addCharacter(gfxChar);
        }});

        processor.processByte((byte) 27);
        processor.processByte((byte) Character.codePointAt("t", 0));
        processor.processByte((byte) Character.codePointAt("s", 0));
        processor.processByte((byte) Character.codePointAt("3", 0));
    }

    @Test
    public void testCarridgeReturn() {
        final Sequence seq = context.sequence("seq");

        context.checking(new Expectations() {{
            expectChar('x');
            oneOf(buffer).getCursorPosition(); will(returnValue(new Position(5, 7)));
            oneOf(buffer).setCursorPosition(new Position(1, 7));
            expectChar('z');
            oneOf(buffer).getCursorPosition(); will(returnValue(new Position(9, 12)));
            oneOf(buffer).setCursorPosition(new Position(1, 12));
            expectChar('y');
        }

        private void expectChar(final char x) {
            oneOf(setup).createChar(x);
                inSequence(seq);
                will(returnValue(gfxChar));
            oneOf(buffer).addCharacter(gfxChar);
                inSequence(seq);
        }});

        processor.processByte((byte) Character.codePointAt("x", 0));
        processor.processByte((byte) Character.codePointAt("\r", 0));
        processor.processByte((byte) Character.codePointAt("z", 0));
        processor.processByte((byte) Character.codePointAt("\r", 0));
        processor.processByte((byte) Character.codePointAt("y", 0));
    }

    @Test
    public void testBackspace() {
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition(); will(returnValue(new Position(5, 7)));
            oneOf(buffer).setCursorPosition(new Position(4, 7));
            oneOf(buffer).getCursorPosition(); will(returnValue(new Position(9, 12)));
            oneOf(buffer).setCursorPosition(new Position(8, 12));
        }});

        processor.processByte((byte) 8);
        processor.processByte((byte) 8);
    }

    @Test
    public void testBell() {
        final Sequence seq = context.sequence("seq");

        context.checking(new Expectations() {{
            expectChar('x');
            oneOf(visualFeedback).bell();
            expectChar('z');
        }

        private void expectChar(final char x) {
            oneOf(setup).createChar(x);
                inSequence(seq);
                will(returnValue(gfxChar));
            oneOf(buffer).addCharacter(gfxChar);
                inSequence(seq);
        }});

        processor.processByte((byte) Character.codePointAt("x", 0));
        processor.processByte((byte) 7);
        processor.processByte((byte) Character.codePointAt("z", 0));
    }

    @Test
    public void testStackStates() {
        final Sequence seq = context.sequence("seq");
        context.checking(new Expectations() {{
                oneOf(buffer).addNewLine();
                    inSequence(seq);

                handleSeq(sequence1, "1", false);
                handleSeq(sequence2, "1", false);
                handlePartialStart(sequence1, "1", false);
                handlePartialStart(sequence2, "1", true);
                handleSeq(sequence2, "12", true);
                oneOf(sequence2).handleSequence("12", sessionInfo);
                    inSequence(seq);

                handleSeq(sequence1, "a", false);
                handleSeq(sequence2, "a", false);
                handlePartialStart(sequence1, "a", true);
                handlePartialStart(sequence2, "a", false);
                handleSeq(sequence1, "ab", true);
                oneOf(sequence1).handleSequence("ab", sessionInfo);
                    inSequence(seq);
            }

            protected void handleSeq(final NonASCIIControlSequence<GfxChar> sequence,
                    final String expected, final boolean value) {
                oneOf(sequence).canHandleSequence(
                        with(new StringBuilderEquals(expected)));
                    will(returnValue(value));
            }

            private void handlePartialStart(
                    final NonASCIIControlSequence<GfxChar> sequence, final String expected,
                    final boolean value) {
                oneOf(sequence).isPartialStart(
                        with(new StringBuilderEquals(expected)));
                    will(returnValue(value));
            }
        });

        processor.processByte((byte) 27);
        processor.processByte((byte) Character.codePointAt("a", 0));
        processor.processByte((byte) 27);
        processor.processByte((byte) '\n');
        processor.processByte((byte) Character.codePointAt("1", 0));
        processor.processByte((byte) Character.codePointAt("2", 0));
        processor.processByte((byte) Character.codePointAt("b", 0));
    }
}
