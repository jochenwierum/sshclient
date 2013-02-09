package de.jowisoftware.sshclient.ui.settings.validation;

import static org.testng.Assert.assertFalse;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.AbstractValidationTest;
import de.jowisoftware.sshclient.application.settings.validation.ValidationResult;

public class AWTFontValidatorTest extends AbstractValidationTest<AWTProfile> {
    @BeforeMethod
    public void setUp() {
        validator = new AWTFontValidator();
    }

    @Test
    public void testDefault() {
        assertNoError();
    }

    @Test
    public void testOk() {
        profile.getGfxSettings().setFont("Monospaced", 8);
        assertNoError();

        profile.getGfxSettings().setFont("Courier New", 12);
        final ValidationResult result1 = doValidation();
        profile.getGfxSettings().setFont("Courier", 12);
        final ValidationResult result2 = doValidation();

        // not all OS'ses have both fonts
        assertFalse(result1.hadErrors() && result2.hadErrors());
    }

    @Test
    public void testWrongFont() {
        final String message = "selected font is no monospace font";
        profile.getGfxSettings().setFont("Times New Roman", 12);
        assertError("gfx.font", message);

        profile.getGfxSettings().setFont("Arial", 12);
        assertError("gfx.font", message);
    }

    @Override
    protected AWTProfile newProfile() {
        return new AWTProfile();
    }
}
