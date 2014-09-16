package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.i18n.Translation;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public abstract class AbstractValidationTest<T extends Profile<?>> {
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

    protected void assertError(final String field, final String message) {
        final ValidationResult errors = doValidation();
        assertThat(errors.hadErrors(), is(true));
        assertThat(errors.getErrors().get(field), is(message));
    }

    protected void assertNoError() {
        assertThat(doValidation().hadErrors(), is(false));
    }
}
