package de.jowisoftware.sshclient.terminal.input;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.input.controlsequences.NonASCIIControlSequence;
import de.jowisoftware.sshclient.terminal.input.controlsequences.SequenceRepository;

@RunWith(JMock.class)
public class CharacterProcessorTest {
    private final Mockery context = new JUnit4Mockery();
    private Buffer buffer;
    private GfxCharSetup setup;
    private NonASCIIControlSequence sequence1;
    private NonASCIIControlSequence sequence2;
    private GfxChar gfxChar;
    private CharacterProcessor processor;
    private VisualEvent visualFeedback;
    private SSHSession sessionInfo;
    private SequenceRepository repository;

    @Before
    public void setUp() {
        buffer = context.mock(Buffer.class);
        setup = context.mock(GfxCharSetup.class);
        visualFeedback = context.mock(VisualEvent.class);
        sequence1 = context.mock(NonASCIIControlSequence.class, "sequence1");
        sequence2 = context.mock(NonASCIIControlSequence.class, "sequence2");
        gfxChar = context.mock(GfxChar.class);
        sessionInfo = context.mock(SSHSession.class);
        repository = context.mock(SequenceRepository.class);
        final EventHub<?> eventHub = context.mock(EventHub.class);

        processor = new CharacterProcessor(
                sessionInfo,
                Charset.defaultCharset(),
                repository);

        context.checking(new Expectations() {{
            allowing(sessionInfo).getBuffer(); will(returnValue(buffer));
            allowing(sessionInfo).getCharSetup(); will(returnValue(setup));
            allowing(repository).getNonASCIISequences();
                will(returnValue(Arrays.asList(sequence1, sequence2)));
            allowing(sessionInfo).getVisualFeedback(); will(returnValue(eventHub));
            allowing(eventHub).fire(); will(returnValue(visualFeedback));
        }});
    }

    private void handleSeq(final NonASCIIControlSequence sequence,
            final String expected, final boolean value, final Expectations exp) {
        exp.oneOf(sequence).canHandleSequence(expected);
            exp.will(Expectations.returnValue(value));
    }

    private void handlePartialStart(
            final NonASCIIControlSequence sequence, final String expected,
            final boolean value, final Expectations exp) {
        exp.oneOf(sequence).isPartialStart(expected);
            exp.will(Expectations.returnValue(value));
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
            oneOf(buffer).moveCursorDown(true); inSequence(seq);
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
            oneOf(sequence1).canHandleSequence("t");
                will(returnValue(false));
            oneOf(sequence2).canHandleSequence("t");
                will(returnValue(false));
            oneOf(sequence1).isPartialStart("t");
                will(returnValue(true));
            oneOf(sequence2).isPartialStart("t");
                will(returnValue(false));
            oneOf(sequence1).canHandleSequence("ts");
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
            oneOf(buffer).processBackspace();
        }});

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
                oneOf(buffer).moveCursorDown(true);
                    inSequence(seq);

                handleSeq(sequence1, "1", false, this);
                handleSeq(sequence2, "1", false, this);
                handlePartialStart(sequence1, "1", false, this);
                handlePartialStart(sequence2, "1", true, this);
                handleSeq(sequence2, "12", true, this);
                oneOf(sequence2).handleSequence("12", sessionInfo);
                    inSequence(seq);

                handleSeq(sequence1, "a", false, this);
                handleSeq(sequence2, "a", false, this);
                handlePartialStart(sequence1, "a", true, this);
                handlePartialStart(sequence2, "a", false, this);
                handleSeq(sequence1, "ab", true, this);
                oneOf(sequence1).handleSequence("ab", sessionInfo);
                    inSequence(seq);
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


    @Test
    public void escapeBackslashIsForwardedInStackedStates() {
        context.checking(new Expectations() {{
                handleSeq(sequence1, "a", false, this);
                handleSeq(sequence2, "a", false, this);
                handlePartialStart(sequence1, "a", true, this);
                handlePartialStart(sequence2, "a", false, this);

                handleSeq(sequence1, "a\u001b", false, this);
                handlePartialStart(sequence1, "a\u001b", true, this);

                handleSeq(sequence1, "a\u001b\\", true, this);
                oneOf(sequence1).handleSequence("a\u001b\\", sessionInfo);
            }
        });

        processor.processByte((byte) 27);
        processor.processByte((byte) Character.codePointAt("a", 0));
        processor.processByte((byte) 27);
        processor.processByte((byte) '\\');
    }
}
