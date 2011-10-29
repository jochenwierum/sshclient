package de.jowisoftware.sshclient.ui.validation;

import static org.junit.Assert.assertFalse;

import java.awt.Font;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.settings.validation.ValidationResult;
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
    public void testNull() {
        profile.getGfxSettings().setFont(null);
        assertError("gfx.font", "no font selected");
    }

    @Test
    public void testOk() {
        profile.getGfxSettings().setFont(new Font("Monospaced", Font.BOLD, 8));
        assertNoError();

        profile.getGfxSettings().setFont(new Font("Courier New", 0, 12));
        final ValidationResult result1 = doValidation();
        profile.getGfxSettings().setFont(new Font("Courier", 0, 12));
        final ValidationResult result2 = doValidation();

        // not all OS'ses have both fonts
        assertFalse(result1.hadErrors() && result2.hadErrors());
    }

    @Test
    public void testWrongFont() {
        final String message = "selected font is no monospace font";
        profile.getGfxSettings().setFont(new Font("Times New Roman", 0, 12));
        assertError("gfx.font", message);

        profile.getGfxSettings().setFont(new Font("Arial", 0, 12));
        assertError("gfx.font", message);
    }

    @Override
    protected AWTProfile newProfile() {
        return new AWTProfile();
    }
}
