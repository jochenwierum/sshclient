package de.jowisoftware.sshclient.terminal.buffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class PositionTest {
    private Position position(final int x, final int y) {
        return new Position(x, y);
    }

    @Test
    public void testConstructor1() {
        final Position pos = position(1, 3);
        assertEquals(1, pos.x);
        assertEquals(3, pos.y);
    }

    @Test
    public void testConstructor2() {
        final Position pos = position(6, 7);
        assertEquals(6, pos.x);
        assertEquals(7, pos.y);
    }

    @Test
    public void testOffset1() {
        final Position pos = position(3, 5);
        final Position pos2 = pos.offset(-1, 1);
        assertNotSame(pos, pos2);
        assertEquals(2, pos2.x);
        assertEquals(6, pos2.y);
    }

    @Test
    public void testOffset2() {
        final Position pos = position(2, 9);
        final Position pos2 = pos.offset(22, 5);
        assertNotSame(pos, pos2);
        assertEquals(24, pos2.x);
        assertEquals(14, pos2.y);
    }

    @Test
    public void testEquals() {
        final Position pos1 = position(1, 5);
        final Position pos2 = position(2, 5);
        final Position pos3 = position(2, 4);
        final Position pos4 = position(1, 1);
        final Position pos5 = position(1, 5);
        final Position pos6 = position(2, 4);

        assertEquals(pos1, pos5);
        assertEquals(pos5, pos1);
        assertEquals(pos3, pos6);
        assertFalse(pos2.equals(pos1));
        assertFalse(pos4.equals(pos1));
    }

    @Test
    public void testWithY() {
        final Position pos1 = position(1, 5);
        final Position pos2 = position(1, 3);
        final Position pos3 = position(6, 4);
        final Position pos4 = position(6, 3);

        assertEquals(pos2, pos1.withY(3));
        assertEquals(pos1, pos2.withY(5));
        assertEquals(pos4, pos3.withY(3));

        assertFalse(pos1.withY(9).equals(pos4));
        assertSame(pos1, pos1.withY(5));
        assertSame(pos2, pos2.withY(3));
    }

    @Test
    public void testWithX() {
        final Position pos1 = position(5, 1);
        final Position pos2 = position(3, 1);
        final Position pos3 = position(4, 6);
        final Position pos4 = position(3, 6);

        assertEquals(pos2, pos1.withX(3));
        assertEquals(pos1, pos2.withX(5));
        assertEquals(pos4, pos3.withX(3));

        assertFalse(pos1 .withX(9).equals(pos4));
        assertSame(pos1, pos1.withX(5));
        assertSame(pos2, pos2.withX(3));
    }

    @Test
    public void testValidPositionsBottomRight() {
        final Position pos1 = position(10, 10);
        final Position pos2 = position(8, 15);

        final Range range1 = position(12, 12).toRange();
        final Range range2 = position(5, 12).toRange();
        final Range range3 = new Range(position(2, 2), position(5, 20));

        assertSame(pos1, pos1.moveInRange(range1));
        assertEquals(position(5, 10), pos1.moveInRange(range2));
        assertEquals(position(8, 12), pos2.moveInRange(range1));
        assertEquals(position(5, 15), pos2.moveInRange(range3));
    }

    @Test
    public void testValidPositionsTopLeft() {
        final Position pos1 = position(-2, 10);
        final Position pos2 = position(1, 9);
        final Position pos3 = position(1, 1);

        final Range range1 = position(12, 12).toRange();
        final Range range2 = new Range(position(2, 3), position(5, 12));

        assertSame(pos2, pos2.moveInRange(range1));
        assertEquals(position(1, 10), pos1.moveInRange(range1));
        assertEquals(position(2, 10), pos1.moveInRange(range2));
        assertEquals(position(2, 3), pos3.moveInRange(range2));
    }

    @Test
    public void testToRange() {
        final Position pos1 = position(3, 7);
        final Range range1 = pos1.toRange();

        assertEquals(position(1, 1), range1.topLeft);
        assertEquals(pos1, range1.bottomRight);

        final Position pos2 = position(5, 9);
        final Range range2 = pos2.toRange();

        assertEquals(position(1, 1), range2.topLeft);
        assertEquals(pos2, range2.bottomRight);
    }

    @Test
    public void testBefore() {
        assertThat(position(4, 5).isBefore(position(8, 20)), is(equalTo(true)));
        assertThat(position(3, 2).isBefore(position(4, 2)), is(equalTo(true)));
        assertThat(position(6, 10).isBefore(position(6, 10)), is(equalTo(false)));
        assertThat(position(7, 10).isBefore(position(6, 10)), is(equalTo(false)));
        assertThat(position(4, 5).isBefore(position(3, 2)), is(equalTo(false)));
    }

    @Test
    public void testAfter() {
        assertThat(position(7, 10).isAfter(position(6, 10)), is(equalTo(true)));
        assertThat(position(4, 5).isAfter(position(3, 2)), is(equalTo(true)));
        assertThat(position(4, 5).isAfter(position(8, 20)), is(equalTo(false)));
        assertThat(position(3, 2).isAfter(position(4, 2)), is(equalTo(false)));
        assertThat(position(6, 10).isAfter(position(6, 10)), is(equalTo(false)));
    }
}
