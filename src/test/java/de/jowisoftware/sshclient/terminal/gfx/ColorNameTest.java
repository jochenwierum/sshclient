package de.jowisoftware.sshclient.terminal.gfx;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorNameTest {
    @Test
    public void findForegroundRed() {
        final ColorName color = ColorName.find(31);
        assertNotNull(color);
        assertEquals(ColorName.RED, color);
        assertEquals("red", color.niceName());
    }

    @Test
    public void findBackgroundBlue() {
        final ColorName color = ColorName.find(44);
        assertNotNull(color);
        assertEquals(ColorName.BLUE, color);
        assertEquals("blue", color.niceName());
    }

    @Test
    public void findUnknownColor() {
        final ColorName color = ColorName.find(7);
        assertNull(color);
    }

    @Test
    public void identifyBackgroundColors() {
        assertTrue(ColorName.isForeground(30));
        assertTrue(ColorName.isForeground(35));
        assertTrue(ColorName.isForeground(39));
        assertFalse(ColorName.isForeground(40));
        assertFalse(ColorName.isForeground(43));
        assertFalse(ColorName.isForeground(49));
    }
}
