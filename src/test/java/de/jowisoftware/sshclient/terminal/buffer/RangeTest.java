package de.jowisoftware.sshclient.terminal.buffer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

public class RangeTest {
    @Test
    public void test1ArgConstructor() {
        final Range range = new Position(3, 5).toRange();

        assertEquals(range.topLeft, new Position(1, 1));
        assertEquals(new Position(3, 5), range.bottomRight);

        final Range range2 = new Position(4, 8).toRange();
        assertEquals(new Position(1, 1), range2.topLeft);
        assertEquals(new Position(4, 8), range2.bottomRight);
    }

    @Test
    public void test2ArgConstructor() {
        final Range range = new Range(new Position(2, 4), new Position(5, 12));

        assertEquals(new Position(2, 4), range.topLeft);
        assertEquals(new Position(5, 12), range.bottomRight);

        final Range range2 = new Range(new Position(12, 9), new Position(1, 17));
        assertEquals(new Position(1, 9), range2.topLeft);
        assertEquals(new Position(12, 17), range2.bottomRight);
    }

    @Test
    public void testEquals() {
        final Range range1 = new Range(new Position(3, 4), new Position(4, 8));
        final Range range2 = new Range(new Position(3, 4), new Position(4, 8));

        final Range range3 = new Range(new Position(1, 5), new Position(3, 3));
        final Range range4 = new Range(new Position(1, 5), new Position(3, 3));

        assertEquals(range1, range1);
        assertEquals(range1, range2);
        assertEquals(range2, range1);
        assertEquals(range3, range4);

        assertFalse(range1.equals(range3));
        assertFalse(range4.equals(range1));
    }

    @Test
    public void testWidthAndHeight() {
        final Range range1 = new Range(new Position(1, 1), new Position(3, 8));
        final Range range2 = new Range(new Position(2, 3), new Position(7, 4));

        assertEquals(3, range1.width());
        assertEquals(6, range2.width());
        assertEquals(8, range1.height());
        assertEquals(2, range2.height());
    }

    @Test
    public void testOffset() {
        final Range range1 = new Range(new Position(1, 1), new Position(3, 8));
        final Range range2 = new Range(new Position(2, 3), new Position(7, 4));

        final Range range3 = new Range(new Position(3, 5), new Position(5, 12));
        final Range range4 = new Range(new Position(1, 2), new Position(6, 3));

        assertEquals(range3, range1.offset(2, 4));
        assertEquals(range4, range2.offset(-1, -1));
    }
}
