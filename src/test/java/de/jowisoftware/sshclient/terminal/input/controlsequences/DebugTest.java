package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jmock.Expectations;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.gfx.GfxChar;

public class DebugTest extends AbstractSequenceTest {
    @Test
    public void testFillWithEs() {
        final GfxChar gfxChar = context.mock(GfxChar.class);
        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            atLeast(1).of(charSetup).createChar('E'); will(returnValue(gfxChar));
            oneOf(buffer).getSize(); will(returnValue(new Position(2, 3)));
            oneOf(buffer).setCursorPosition(new Position(1, 1));
            oneOf(buffer).addCharacter(gfxChar); oneOf(buffer).addCharacter(gfxChar);
            oneOf(buffer).moveCursorDown(true);
            oneOf(buffer).addCharacter(gfxChar); oneOf(buffer).addCharacter(gfxChar);
            oneOf(buffer).moveCursorDown(true);
            oneOf(buffer).addCharacter(gfxChar); oneOf(buffer).addCharacter(gfxChar);
        }});

        final DebugControlSequence seq = new DebugControlSequence();
        seq.handleSequence("#8", sessionInfo);

        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            oneOf(buffer).getSize(); will(returnValue(new Position(3, 2)));
            oneOf(buffer).setCursorPosition(new Position(1, 1));
            oneOf(buffer).addCharacter(gfxChar); oneOf(buffer).addCharacter(gfxChar);
            oneOf(buffer).addCharacter(gfxChar); oneOf(buffer).moveCursorDown(true);
            oneOf(buffer).addCharacter(gfxChar); oneOf(buffer).addCharacter(gfxChar);
            oneOf(buffer).addCharacter(gfxChar);
        }});

        seq.handleSequence("#8", sessionInfo);
    }

    @Test
    public void testHandlePartial() {
        final DebugControlSequence seq = new DebugControlSequence();
        assertTrue(seq.isPartialStart("#"));
        assertFalse(seq.isPartialStart("8"));
    }

    @Test
    public void testHandleDebugWithE() {
        final DebugControlSequence seq = new DebugControlSequence();
        assertTrue(seq.canHandleSequence("#8"));
    }
}
