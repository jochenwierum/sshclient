package de.jowisoftware.sshclient.application.settings.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.validation.DefaultValidationResult;
import de.jowisoftware.sshclient.application.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.application.settings.validation.Validator;
import de.jowisoftware.sshclient.i18n.Translation;

public abstract class ValidationTest<T extends Profile<?>> {
    protected T profile;
    protected Validator<T> validator;

    @BeforeClass
    public static void setUpTranslation() {
        Translation.initStaticTranslationWithLanguage(null);
    }

    @Before
    public void setUpProfile() {
        profile = newProfile();
    }

    protected abstract T newProfile();

    protected ValidationResult doValidation() {
        final ValidationResult result = new DefaultValidationResult();
        validator.validate(profile, result);
        return result;
    }

    public void assertError(final String field, final String message) {
        final ValidationResult errors = doValidation();
        assertTrue(errors.hadErrors());
        assertEquals(message, errors.getErrors().get(field));
    }

    public void assertNoError() {
        assertFalse(doValidation().hadErrors());
    }
}
