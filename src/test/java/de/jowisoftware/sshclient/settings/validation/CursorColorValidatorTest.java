package de.jowisoftware.sshclient.settings.validation;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

public class CursorColorValidatorTest extends ValidationTest {
    @Before
    public void setUp() {
        validator = new CursorColorValidator();
    }

    @Test
    public void testMissingColor() {
        profile.getGfxSettings().setCursorColor(null);
        assertError("gfx.cursorcolor", "cursor color is missing");
    }

    @Test
    public void testAllOk() {
        assertNoError();

        profile.getGfxSettings().setCursorColor(Color.RED);
        assertNoError();
    }
}
