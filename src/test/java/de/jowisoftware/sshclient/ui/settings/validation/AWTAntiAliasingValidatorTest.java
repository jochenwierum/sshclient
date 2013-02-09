package de.jowisoftware.sshclient.ui.settings.validation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.AbstractValidationTest;
import de.jowisoftware.sshclient.util.FontUtils;

public class AWTAntiAliasingValidatorTest extends AbstractValidationTest<AWTProfile> {
    @BeforeMethod
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
