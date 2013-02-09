package de.jowisoftware.sshclient.application.settings.validation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.i18n.Translation;

public abstract class AbstractValidationTest<T extends Profile<?>> {
    protected T profile;
    protected Validator<T> validator;

    @BeforeClass
    public static void setUpTranslation() {
        Translation.initStaticTranslationWithLanguage(null);
    }

    @BeforeMethod
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
