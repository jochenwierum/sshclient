package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.junit.Test;

import de.jowisoftware.sshclient.util.SequenceUtils;

public class ANSISequenceScreenManipulationTest extends AbstractSequenceTest {
    @Test
    public void testInsertLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).insertLines(1);
        }});
        SequenceUtils.getANSISequence('L').process(sessionInfo);
    }

    @Test
    public void testInsert2Lines() {
        context.checking(new Expectations() {{
            oneOf(buffer).insertLines(2);
        }});

        SequenceUtils.getANSISequence('L').process(sessionInfo, "2");
    }
}
