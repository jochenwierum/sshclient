package de.jowisoftware.sshclient.terminal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class PositionTest {
    @Test
    public void testConstructor1() {
        final Position pos = new Position(1, 3);
        assertEquals(1, pos.x);
        assertEquals(3, pos.y);
    }

    @Test
    public void testConstructor2() {
        final Position pos = new Position(6, 7);
        assertEquals(6, pos.x);
        assertEquals(7, pos.y);
    }

    @Test
    public void testOffset1() {
        final Position pos = new Position(3, 5);
        final Position pos2 = pos.offset(-1, 1);
        assertNotSame(pos, pos2);
        assertEquals(2, pos2.x);
        assertEquals(6, pos2.y);
    }

    @Test
    public void testOffset2() {
        final Position pos = new Position(2, 9);
        final Position pos2 = pos.offset(22, 5);
        assertNotSame(pos, pos2);
        assertEquals(24, pos2.x);
        assertEquals(14, pos2.y);
    }

    @Test
    public void testEquals() {
        final Position pos1 = new Position(1, 5);
        final Position pos2 = new Position(2, 5);
        final Position pos3 = new Position(2, 4);
        final Position pos4 = new Position(1, 1);
        final Position pos5 = new Position(1, 5);
        final Position pos6 = new Position(2, 4);

        assertEquals(pos1, pos5);
        assertEquals(pos5, pos1);
        assertEquals(pos3, pos6);
        assertFalse(pos2.equals(pos1));
        assertFalse(pos4.equals(pos1));
    }

    @Test
    public void testWithY() {
        final Position pos1 = new Position(1, 5);
        final Position pos2 = new Position(1, 3);
        final Position pos3 = new Position(6, 4);
        final Position pos4 = new Position(6, 3);

        assertEquals(pos2, pos1.withY(3));
        assertEquals(pos1, pos2.withY(5));
        assertEquals(pos4, pos3.withY(3));

        assertFalse(pos1.withY(9).equals(pos4));
        assertSame(pos1, pos1.withY(5));
        assertSame(pos2, pos2.withY(3));
    }

    @Test
    public void testWithX() {
        final Position pos1 = new Position(5, 1);
        final Position pos2 = new Position(3, 1);
        final Position pos3 = new Position(4, 6);
        final Position pos4 = new Position(3, 6);

        assertEquals(pos2, pos1.withX(3));
        assertEquals(pos1, pos2.withX(5));
        assertEquals(pos4, pos3.withX(3));

        assertFalse(pos1 .withX(9).equals(pos4));
        assertSame(pos1, pos1.withX(5));
        assertSame(pos2, pos2.withX(3));
    }

    @Test
    public void testValidPositionsBottomRight() {
        final Position pos1 = new Position(10, 10);
        final Position pos2 = new Position(8, 15);

        final Range range1 = new Range(new Position(12, 12));
        final Range range2 = new Range(new Position(5, 12));
        final Range range3 = new Range(new Position(2, 2), new Position(5, 20));

        assertSame(pos1, pos1.moveInRange(range1));
        assertEquals(new Position(5, 10), pos1.moveInRange(range2));
        assertEquals(new Position(8, 12), pos2.moveInRange(range1));
        assertEquals(new Position(5, 15), pos2.moveInRange(range3));
    }

    @Test
    public void testValidPositionsTopLeft() {
        final Position pos1 = new Position(-2, 10);
        final Position pos2 = new Position(1, 9);
        final Position pos3 = new Position(1, 1);

        final Range range1 = new Range(new Position(12, 12));
        final Range range2 = new Range(new Position(2, 3), new Position(5, 12));

        assertSame(pos2, pos2.moveInRange(range1));
        assertEquals(new Position(1, 10), pos1.moveInRange(range1));
        assertEquals(new Position(2, 10), pos1.moveInRange(range2));
        assertEquals(new Position(2, 3), pos3.moveInRange(range2));
    }
}
