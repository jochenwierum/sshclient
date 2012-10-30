package de.jowisoftware.sshclient.terminal.buffer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class DefaultCursorPositionManagerTest {
    private final Mockery context = new JUnit4Mockery();
    private CursorPositionManagerFeedback feedback;
    private CursorPositionManager manager;

    @Before
    public void setUp() {
        feedback = context.mock(CursorPositionManagerFeedback.class, "feedback");
        manager = new DefaultCursorPositionManager(feedback, 80, 24);
    }

    private void assertPosition(final int y, final int x) {
        assertThat(manager.currentPositionInScreen(), equalTo(new Position(x, y)));
    }

    @Test public void
    settingCursorPosition() {
        manager.setPositionSafelyInScreen(new Position(2, 4));
        assertPosition(4, 2);

        manager.setPositionSafelyInMargin(new Position(1, 1));
        assertPosition(1, 1);
    }

    @Test public void
    settingPositionInMarginScrollsDown() {
        manager.setMargins(12, 14);
        context.checking(new Expectations() {{
            oneOf(feedback).lineShiftingNeeded(-5, 12, 14);
        }});

        manager.setPositionSafelyInMargin(new Position(1, 8));
        assertPosition(14, 1);
    }

    @Test public void
    settingPositionInMarginScrollsUp() {
        manager.setMargins(13, 14);
        context.checking(new Expectations() {{
            oneOf(feedback).lineShiftingNeeded(3, 13, 14);
        }});

        manager.setPositionSafelyInMargin(new Position(1, -2));
        assertPosition(13, 1);
    }

    @Test public void
    resizingTo30x24KeepsPosition() {
        manager.setPositionSafelyInScreen(new Position(10, 11));
        manager.newSize(30, 24);
        assertPosition(11, 10);
    }

    @Test public void
    resizingTo5x6TruncatesPosition() {
        manager.setPositionSafelyInScreen(new Position(10, 11));
        manager.newSize(5, 6);
        assertPosition(6, 5);
    }

    @Test public void
    fullBufferTriggersRoll() {
        context.checking(new Expectations() {{
            oneOf(feedback).lineShiftingNeeded(-1, 0, 24);
        }});

        manager.setPositionSafelyInScreen(new Position(1, 25));
        assertPosition(24, 1);
    }


    @Test public void
    fullBufferTriggersRollFarJump() {
        manager.newSize(80, 28);
        context.checking(new Expectations() {{
            oneOf(feedback).lineShiftingNeeded(-3, 0, 28);
        }});

        manager.setPositionSafelyInScreen(new Position(5, 31));
        assertPosition(28, 5);
    }

    @Test public void
    testMoveCursorUpAndRollTriggersLineshift() {
        manager.setMargins(2, 3);
        assertPosition(1, 1);
        manager.setPositionSafelyInScreen(new Position(2, 3));

        manager.moveUpAndRoll();
        assertPosition(2, 2);

        manager.setPositionSafelyInScreen(new Position(1, 2));
        context.checking(new Expectations() {{
            oneOf(feedback).lineShiftingNeeded(1, 1, 3);
        }});

        manager.moveUpAndRoll();
        assertPosition(2, 1);
    }

    @Test public void
    moveCursorUpAndRollWithoutMarginsTriggersLineshift() {
        manager.setPositionSafelyInScreen(new Position(3, 1));
        context.checking(new Expectations() {{
            oneOf(feedback).lineShiftingNeeded(1, 0, 24);
        }});

        manager.moveUpAndRoll();
        assertPosition(1, 3);
    }

    @Test public void
    testMoveCursorUpAndRollWithoutRoll() {
        manager.setPositionSafelyInScreen(new Position(2, 4));
        manager.moveUpAndRoll();
        assertPosition(3, 2);
        manager.moveUpAndRoll();
        manager.moveUpAndRoll();
        assertPosition(1, 2);
    }

    @Test public void
    moveCursorDownWithoutRoll() {
        manager.setPositionSafelyInScreen(new Position(2, 3));
        manager.moveDownAndRoll();
        assertPosition(4, 2);
        manager.moveDownAndRoll();
        manager.moveDownAndRoll();
        assertPosition(6, 2);
    }

    @Test
    public void testMoveCursorDownAndRollWithoutMargins() {
        manager.setPositionSafelyInScreen(new Position(3, 24));
        context.checking(new Expectations() {{
            oneOf(feedback).lineShiftingNeeded(-1, 0, 24);
        }});

        manager.moveDownAndRoll();
        assertPosition(24, 3);
    }


    @Test public void
    moveCursorDownAndRollShiftingLinesIfNeeded() {
        manager.setMargins(2, 3);
        manager.setPositionSafelyInScreen(new Position(2, 2));

        manager.moveDownAndRoll();
        assertPosition(3, 2);

        context.checking(new Expectations() {{
            oneOf(feedback).lineShiftingNeeded(-1, 1, 3);
        }});

        manager.moveDownAndRoll();
        assertPosition(3, 2);
    }

    @Test public void
    saveAndRestoreKeepsPosition() {
        manager.setPositionSafelyInScreen(new Position(22, 5));
        manager.save();
        assertPosition(5, 22);
        manager.setPositionSafelyInScreen(new Position(1, 1));

        manager.restore();
        assertPosition(5, 22);

        manager.setPositionSafelyInScreen(new Position(7, 7));
        manager.save();
        manager.setPositionSafelyInScreen(new Position(9, 9));
        manager.restore();
        assertPosition(7, 7);
    }

    @Test
    public void marginIsExported() {
        manager.setPositionSafelyInScreen(new Position(3, 7));

        manager.setMargins(4, 8);
        assertThat(manager.getBottomMargin(), is(equalTo(8)));
        assertThat(manager.currentPositionInMargin(), is(equalTo(new Position(3, 4))));

        manager.setMargins(6, 9);
        assertThat(manager.getBottomMargin(), is(equalTo(9)));
        assertThat(manager.currentPositionInMargin(), is(equalTo(new Position(3, 2))));

    }

    @Test
    public void moveToNextPositionUpdatesCursorInformation() {
        manager.setPositionSafelyInScreen(new Position(63, 4));

        manager.moveToNextPosition(1);
        assertThat(manager.wouldWrap(), is(equalTo(false)));
        assertThat(manager.currentPositionInScreen(), is(equalTo(new Position(64, 4))));

        manager.setPositionSafelyInScreen(new Position(79, 1));
        manager.moveToNextPosition(1);
        assertThat(manager.wouldWrap(), is(equalTo(false)));
        assertThat(manager.currentPositionInScreen(), is(equalTo(new Position(80, 1))));

        manager.moveToNextPosition(1);
        assertThat(manager.wouldWrap(), is(equalTo(true)));
        assertThat(manager.currentPositionInScreen(), is(equalTo(new Position(80, 1))));

        manager.resetWouldWrap();
        assertThat(manager.wouldWrap(), is(equalTo(false)));

        manager.setPositionSafelyInScreen(new Position(63, 4));
        manager.moveToNextPosition(2);
        assertThat(manager.wouldWrap(), is(equalTo(false)));
        assertThat(manager.currentPositionInScreen(), is(equalTo(new Position(65, 4))));
    }

    @Test
    public void settingTheCursorResetsWouldWrap() {
        manager.setPositionSafelyInScreen(new Position(80, 1));
        manager.moveToNextPosition(1);
        manager.setPositionSafelyInScreen(new Position(80, 1));
        assertThat(manager.wouldWrap(), is(equalTo(false)));

        manager.setPositionSafelyInScreen(new Position(80, 1));
        manager.moveToNextPosition(1);
        manager.setPositionSafelyInMargin(new Position(80, 1));
        assertThat(manager.wouldWrap(), is(equalTo(false)));
    }
}
