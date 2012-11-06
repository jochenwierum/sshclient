package de.jowisoftware.sshclient.ui.settings.validation;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.validation.ValidationResult;
import de.jowisoftware.sshclient.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.settings.validation.ValidationTest;
import de.jowisoftware.sshclient.ui.settings.validation.AWTFontValidator;

public class AWTFontValidatorTest extends ValidationTest<AWTProfile> {
    @Before
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
