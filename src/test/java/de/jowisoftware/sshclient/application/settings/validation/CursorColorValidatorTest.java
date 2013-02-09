package de.jowisoftware.sshclient.application.settings.validation;

import java.awt.Color;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class CursorColorValidatorTest extends AbstractValidationTest<AWTProfile> {
    @BeforeMethod
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
