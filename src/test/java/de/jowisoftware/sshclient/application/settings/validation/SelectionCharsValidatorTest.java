package de.jowisoftware.sshclient.application.settings.validation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class SelectionCharsValidatorTest extends AbstractValidationTest<Profile<?>> {
    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }

    @BeforeMethod
    public void createValidator() {
        validator = new SelectionCharsValidator();
    }

    @Test
    public void nonNullValuesAreAllowed() {
        profile.getGfxSettings().setBoundaryChars("test");
        doValidation();
        assertNoError();
    }

    @Test
    public void emptyValuesAreAllowed() {
        profile.getGfxSettings().setBoundaryChars("");
        doValidation();
        assertNoError();
    }

    @Test
    public void nullValuesAreForbidden() {
        profile.getGfxSettings().setBoundaryChars(null);
        doValidation();
        assertError("gfx.boundaryChars", "word boundary characters are missing");
    }
}
