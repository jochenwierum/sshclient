package de.jowisoftware.ssh.client.terminal.controlsequences;

import java.nio.charset.Charset;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.terminal.CursorPosition;
import de.jowisoftware.ssh.client.terminal.Feedback;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;
import de.jowisoftware.ssh.client.test.matches.StringBuilderEquals;
import de.jowisoftware.ssh.client.ui.GfxChar;

@RunWith(JMock.class)
public class CharacterProcessorTest {
    private final Mockery context = new JUnit4Mockery();
    private Buffer<GfxChar> buffer;
    private GfxCharSetup<GfxChar> setup;
    private ControlSequence<GfxChar> sequence1;
    private ControlSequence<GfxChar> sequence2;
    private GfxChar gfxChar;
    private CharacterProcessor<GfxChar> processor;
    private Feedback feedback;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        buffer = context.mock(Buffer.class);
        setup = context.mock(GfxCharSetup.class);
        feedback = context.mock(Feedback.class);
        sequence1 = context.mock(ControlSequence.class, "sequence1");
        sequence2 = context.mock(ControlSequence.class, "sequence2");
        gfxChar = context.mock(GfxChar.class);

        processor = new CharacterProcessor<GfxChar>(buffer,
                setup, Charset.defaultCharset(), feedback);
        processor.addControlSequence(sequence1);
        processor.addControlSequence(sequence2);
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
            oneOf(sequence1).handleSequence("ts", buffer, setup);
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
            oneOf(buffer).getCursorPosition(); will(returnValue(new CursorPosition(5, 7)));
            oneOf(buffer).setCursorPosition(new CursorPosition(0, 7));
            expectChar('z');
            oneOf(buffer).getCursorPosition(); will(returnValue(new CursorPosition(9, 12)));
            oneOf(buffer).setCursorPosition(new CursorPosition(0, 12));
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
            oneOf(buffer).getCursorPosition(); will(returnValue(new CursorPosition(5, 7)));
            oneOf(buffer).setCursorPosition(new CursorPosition(4, 7));
            oneOf(buffer).getCursorPosition(); will(returnValue(new CursorPosition(9, 12)));
            oneOf(buffer).setCursorPosition(new CursorPosition(8, 12));
        }});

        processor.processByte((byte) 8);
        processor.processByte((byte) 8);
    }

    @Test
    public void testBell() {
        final Sequence seq = context.sequence("seq");

        context.checking(new Expectations() {{
            expectChar('x');
            oneOf(feedback).bell();
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
}
