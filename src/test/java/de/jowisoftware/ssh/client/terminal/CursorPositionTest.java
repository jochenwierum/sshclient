package de.jowisoftware.ssh.client.terminal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jowisoftware.ssh.client.terminal.CursorPosition;

public class CursorPositionTest {
    @Test
    public void testConstructor1() {
        final CursorPosition pos = new CursorPosition(1, 3);
        assertEquals(1, pos.getX());
        assertEquals(3, pos.getY());
    }

    @Test
    public void testConstructor2() {
        final CursorPosition pos = new CursorPosition(6, 7);
        assertEquals(6, pos.getX());
        assertEquals(7, pos.getY());
    }

    @Test
    public void testOffset1() {
        final CursorPosition pos = new CursorPosition(3, 5);
        final CursorPosition pos2 = pos.offset(-1, 1);
        assertNotSame(pos, pos2);
        assertEquals(pos2.getX(), 2);
        assertEquals(pos2.getY(), 6);
    }

    @Test
    public void testOffset2() {
        final CursorPosition pos = new CursorPosition(2, 9);
        final CursorPosition pos2 = pos.offset(22, 5);
        assertNotSame(pos, pos2);
        assertEquals(pos2.getX(), 24);
        assertEquals(pos2.getY(), 14);
    }

    @Test
    public void testEquals() {
        final CursorPosition pos1 = new CursorPosition(1, 5);
        final CursorPosition pos2 = new CursorPosition(2, 5);
        final CursorPosition pos3 = new CursorPosition(2, 4);
        final CursorPosition pos4 = new CursorPosition(1, 1);
        final CursorPosition pos5 = new CursorPosition(1, 5);
        final CursorPosition pos6 = new CursorPosition(2, 4);

        assertTrue(pos1.equals(pos5));
        assertTrue(pos3.equals(pos6));
        assertFalse(pos2.equals(pos1));
        assertFalse(pos4.equals(pos1));
    }
}
