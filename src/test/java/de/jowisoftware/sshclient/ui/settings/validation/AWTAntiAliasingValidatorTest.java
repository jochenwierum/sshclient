package de.jowisoftware.sshclient.ui.settings.validation;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.AbstractValidationTest;
import de.jowisoftware.sshclient.util.FontUtils;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class AWTAntiAliasingValidatorTest extends AbstractValidationTest<AWTProfile> {
    @Before
    public void setUp() {
        validator = new AWTAntiAliasingValidator();
    }

    @Override
    protected AWTProfile newProfile() {
        return new AWTProfile();
    }

    @Test
    public void antiAliasingValuesAreGreaterOrEqualsZero() {
        profile.getGfxSettings().setAntiAliasingMode(-1);
        doValidation();
        assertError("gfx.antiAliasingMode", "Antialiasing mode is invalid");
    }

    @Test
    public void antiAliasingValuesAreLessThanMaximum() {
        profile.getGfxSettings().setAntiAliasingMode(FontUtils.getRenderingHintMap().size());
        doValidation();
        assertError("gfx.antiAliasingMode", "Antialiasing mode is invalid");
    }

    @Test
    public void antiAliasingValuesAreAllowed() {
        profile.getGfxSettings().setAntiAliasingMode(1);
        doValidation();
        assertNoError();
    }
}
