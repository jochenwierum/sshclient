package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.jmock.Expectations;
import org.junit.Test;

import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenceTabulatorTest extends AbstractSequenceTest {
    @Test
    public void testRemoveTabstopWithoutParameterAtColumn4() {
        prepareRemoveTab(4);
        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo);
    }

    @Test
    public void testRemoveTabstopWithoutParameterAtColumn9() {
        prepareRemoveTab(4);
        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo);
    }

    @Test
    public void testRemoveTabstopWithParameterAtColumn7() {
        prepareRemoveTab(7);
        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo, "0");
    }

    private void prepareRemoveTab(final int x) {
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(x, 9)));
            oneOf(tabstopManager).removeTab(x);
        }});
    }

    @Test
    public void testRemoveAllTabstops() {
        context.checking(new Expectations() {{
            oneOf(tabstopManager).removeAll();
        }});

        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo, "3");
    }

    @Test
    public void testWrongParameterDoesNothing() {
        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo, "2");
    }
}
