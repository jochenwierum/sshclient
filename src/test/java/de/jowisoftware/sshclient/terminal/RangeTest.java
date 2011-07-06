package de.jowisoftware.sshclient.terminal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class RangeTest {
    @Test
    public void test1ArgConstructor() {
        final Range range = new Range(new Position(3, 5));

        assertEquals(range.topLeft, new Position(1, 1));
        assertEquals(new Position(3, 5), range.bottomRight);

        final Range range2 = new Range(new Position(4, 8));
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
}
