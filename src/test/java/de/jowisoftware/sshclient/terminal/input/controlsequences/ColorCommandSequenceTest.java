package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class ColorCommandSequenceTest extends AbstractSequenceTest {
    private NonASCIIControlSequence sequence;

    @Before
    public void setUp() {
        sequence = new ColorCommandSequence();
    }

    @Test
    public void parsesPartialInputCorrectly() {
        assertThat(sequence.isPartialStart("]"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;1"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;25"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;30;"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;10;rgb:2"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;10;rgb:22"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;10;rgb:21/"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;99;rgb:3f/b"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;254;rgb:11/c4"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;255;rgb:15/f6/"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;10;rgb:99/ff/0"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]4;10;rgb:00/00/ba"), is(equalTo(true)));
        assertThat(sequence.isPartialStart("]5"), is(equalTo(false)));
        assertThat(sequence.isPartialStart("]4;2;"), is(equalTo(false)));
        assertThat(sequence.isPartialStart("]4;300"), is(equalTo(false)));
        assertThat(sequence.isPartialStart("]4;10;h"), is(equalTo(false)));
        assertThat(sequence.isPartialStart("]4;10;rgb:g"), is(equalTo(false)));
        assertThat(sequence.isPartialStart("]4;10;rgb:1/"), is(equalTo(false)));
        assertThat(sequence.isPartialStart("]4;256;"), is(equalTo(false)));
    }

    @Test
    public void parsesCompleteInputCorrectly() {
        assertThat(sequence.canHandleSequence("]"), is(equalTo(false)));
        assertThat(sequence.canHandleSequence("]4;10;rgb:2"), is(equalTo(false)));
        assertThat(sequence.canHandleSequence("]4;10;rgb:21/"), is(equalTo(false)));

        assertThat(sequence.canHandleSequence("]4;254;rgb:11/c4/15\u001b\\"), is(equalTo(true)));
        assertThat(sequence.canHandleSequence("]4;255;rgb:15/f6/3a\u001b\\"), is(equalTo(true)));
        assertThat(sequence.canHandleSequence("]4;20;rgb:99/ff/00\u001b\\"), is(equalTo(true)));
        assertThat(sequence.canHandleSequence("]4;106;rgb:00/00/b0\u001b\\"), is(equalTo(true)));
    }

    @Test
    public void sequenceChangesSessionColor106to7025515() {
        context.checking(new Expectations() {{
            oneOf(charSetup).updateCustomColor(160, 112, 255, 21);
        }});

        sequence.handleSequence("]4;160;rgb:70/ff/15\u001b\\", sessionInfo);
    }

    @Test
    public void sequenceChangesSessionColor17to25500255() {
        context.checking(new Expectations() {{
            oneOf(charSetup).updateCustomColor(17, 255, 0, 255);
        }});

        sequence.handleSequence("]4;17;rgb:ff/00/ff\u001b\\", sessionInfo);
    }
}
