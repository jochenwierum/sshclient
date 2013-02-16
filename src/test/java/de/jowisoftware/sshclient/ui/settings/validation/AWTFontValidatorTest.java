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
        profile.getGfxSettings().setFontName("Monospaced");
        profile.getGfxSettings().setFontSize(8);
        assertNoError();

        profile.getGfxSettings().setFontName("Courier New");
        profile.getGfxSettings().setFontSize(12);
        final ValidationResult result1 = doValidation();
        profile.getGfxSettings().setFontName("Courier");
        profile.getGfxSettings().setFontSize(12);
        final ValidationResult result2 = doValidation();

        // not all OS'ses have both fonts
        assertFalse(result1.hadErrors() && result2.hadErrors());
    }

    @Test
    public void testWrongFont() {
        final String message = "selected font is no monospace font";
        profile.getGfxSettings().setFontName("Times New Roman");
        profile.getGfxSettings().setFontSize(12);
        assertError("gfx.font", message);

        profile.getGfxSettings().setFontName("Arial");
        profile.getGfxSettings().setFontSize(12);
        assertError("gfx.font", message);
    }

    @Override
    protected AWTProfile newProfile() {
        return new AWTProfile();
    }
}
