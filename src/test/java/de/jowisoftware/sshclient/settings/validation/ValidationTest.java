package de.jowisoftware.sshclient.settings.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;

import de.jowisoftware.sshclient.i18n.Translation;
import de.jowisoftware.sshclient.terminal.Profile;

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
