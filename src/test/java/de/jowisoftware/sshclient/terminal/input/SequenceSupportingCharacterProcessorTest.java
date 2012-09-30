package de.jowisoftware.sshclient.terminal.input;

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
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.terminal.gfx.GfxChar;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.input.controlsequences.NonASCIIControlSequence;
import de.jowisoftware.sshclient.terminal.input.controlsequences.SequenceRepository;

@RunWith(JMock.class)
public class SequenceSupportingCharacterProcessorTest {
    private final Mockery context = new JUnit4Mockery();
    private Buffer buffer;
    private GfxCharSetup setup;
    private NonASCIIControlSequence sequence1;
    private NonASCIIControlSequence sequence2;
    private GfxChar gfxChar;
    private SequenceSupportingCharacterProcessor processor;
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

        processor = new SequenceSupportingCharacterProcessor(
                sessionInfo,
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

        processor.processChar('x');
        processor.processChar('y');
        processor.processChar('z');
    }

    @Test
    public void testCharsWithNewLine() {
        final Sequence seq = context.sequence("seq");

        context.checking(new Expectations() {{
            expectChar('x');
            oneOf(buffer).moveCursorDown(false); inSequence(seq);
            expectChar('z');
        }

        private void expectChar(final char x) {
            oneOf(setup).createChar(x);
                inSequence(seq);
                will(returnValue(gfxChar));
            oneOf(buffer).addCharacter(gfxChar);
                inSequence(seq);
        }});

        processor.processChar('x');
        processor.processChar('\n');
        processor.processChar('z');
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

        processor.processChar((char) 27);
        processor.processChar('t');
        processor.processChar('s');
        processor.processChar('3');
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

        processor.processChar('x');
        processor.processChar('\r');
        processor.processChar('z');
        processor.processChar('\r');
        processor.processChar('y');
    }

    @Test
    public void testBackspace() {
        context.checking(new Expectations() {{
            oneOf(buffer).processBackspace();
        }});

        processor.processChar((char) 8);
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

        processor.processChar('x');
        processor.processChar((char) 7);
        processor.processChar('z');
    }

    @Test
    public void testStackStates() {
        final Sequence seq = context.sequence("seq");
        context.checking(new Expectations() {{
                oneOf(buffer).moveCursorDown(false);
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

        processor.processChar((char) 27);
        processor.processChar('a');
        processor.processChar((char) 27);
        processor.processChar('\n');
        processor.processChar('1');
        processor.processChar('2');
        processor.processChar('b');
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

        processor.processChar((char) 27);
        processor.processChar('a');
        processor.processChar((char) 27);
        processor.processChar('\\');
    }

    @Test public void
    multipleCharsAreMergedToString() {
        final char[] chars = Character.toChars(0x024B62); // 𤭢

        processor.processChar(chars[0]);

        context.checking(new Expectations() {{
            oneOf(setup).createMultibyteChar("𤭢"); will(returnValue(gfxChar));
            oneOf(buffer).addCharacter(gfxChar);
        }});
        processor.processChar(chars[1]);
    }

    @Test public void
    shiftInIsRecognized() {
        context.checking(new Expectations() {{
            oneOf(setup).selectCharset(TerminalCharsetSelection.G0);
        }});
        processor.processChar((char) 0xf);
    }

    @Test public void
    shiftOutIsRecognized() {
        context.checking(new Expectations() {{
            oneOf(setup).selectCharset(TerminalCharsetSelection.G1);
        }});
        processor.processChar((char) 0xe);
    }
}
