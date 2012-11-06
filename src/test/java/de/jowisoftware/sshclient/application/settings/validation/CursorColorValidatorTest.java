package de.jowisoftware.sshclient.application.settings.validation;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.CursorColorValidator;

public class CursorColorValidatorTest extends ValidationTest<AWTProfile> {
    @Before
    public void setUp() {
        validator = new CursorColorValidator<AWTProfile>();
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

    @Override
    protected AWTProfile newProfile() {
        return new AWTProfile();
    }
}
