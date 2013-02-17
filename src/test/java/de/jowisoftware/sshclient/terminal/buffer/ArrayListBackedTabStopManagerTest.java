package de.jowisoftware.sshclient.terminal.buffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class ArrayListBackedTabStopManagerTest {
    private TabStopManager manager;

    @Before
    public void setUp() {
        manager = new ArrayListBackedTabStopManager(80);
    }

    private void assertNextPosition(final int currentX, final int currentY,
            final int expectedX) {
        final Position currentPosition = new Position(currentX, currentY);
        final Position expectedPosition = new Position(expectedX, currentY);
        assertThat(manager.getNextHorizontalTabPosition(currentPosition),
                is(expectedPosition));
    }

    @Test
    public void defaultValuesIsMultipleOfEight() {
        assertNextPosition(1, 1, 9);
        assertNextPosition(2, 1, 9);
        assertNextPosition(8, 3, 9);
        assertNextPosition(9, 10, 17);
        assertNextPosition(70, 1, 73);
    }

    @Test
    public void lastPositionIsFixedAtRightMargin() {
        assertNextPosition(73, 5, 80);
        assertNextPosition(80, 5, 80);

        manager.newWidth(100);
        assertNextPosition(80, 4, 81);
        assertNextPosition(99, 5, 100);
        assertNextPosition(100, 3, 100);

        manager.newWidth(80);
        assertNextPosition(80, 5, 80);
    }

    @Test
    public void resettingTabstopClearsThemAll() {
        manager.removeAll();
        assertNextPosition(2, 4, 80);
    }

    @Test
    public void customTabs() {
        manager.removeAll();
        manager.addTab(15);
        manager.addTab(45);

        assertNextPosition(1, 7, 15);
        assertNextPosition(14, 7, 15);
        assertNextPosition(15, 7, 45);
        assertNextPosition(45, 7, 80);
    }

    @Test
    public void removeTabs() {
        manager.removeAll();
        manager.addTab(15);
        manager.addTab(30);
        manager.addTab(45);

        manager.removeTab(15);
        assertNextPosition(1, 7, 30);

        manager.removeTab(30);
        manager.removeTab(7);
        assertNextPosition(1, 7, 45);
    }

    @Test
    public void customTabsAreKeptWhenResizing() {
        manager.removeAll();
        manager.addTab(40);
        manager.newWidth(120);

        assertNextPosition(4, 1, 40);
        assertNextPosition(40, 1, 81);
        assertNextPosition(81, 4, 89);
        assertNextPosition(89, 9, 97);
        assertNextPosition(120, 3, 120);
    }

    @Test
    public void multipleTabsAtSamePositionAreNotHarmfull() {
        manager.removeAll();
        manager.addTab(40);
        manager.addTab(40);

        assertNextPosition(3, 3, 40);
        assertNextPosition(40, 3, 80);
    }

    @Test
    public void afterAddingTheListIsStillSorted() {
        manager.removeAll();
        manager.addTab(80);
        manager.addTab(20);
        manager.addTab(40);

        assertNextPosition(1, 1, 20);
        assertNextPosition(20, 1, 40);
    }
}
