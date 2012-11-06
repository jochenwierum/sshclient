package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.validation.SelectionCharsValidator;
import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;

public class SelectionCharsValidatorTest extends ValidationTest<Profile<?>> {
    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }

    @Before
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
