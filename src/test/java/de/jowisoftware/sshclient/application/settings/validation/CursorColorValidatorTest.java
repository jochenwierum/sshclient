package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.awt.*;

@RunWith(JUnitParamsRunner.class)
public class CursorColorValidatorTest extends AbstractValidationTest<AWTProfile> {
    @Before
    public void setUp() {
        validator = new CursorColorValidator<>();
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
