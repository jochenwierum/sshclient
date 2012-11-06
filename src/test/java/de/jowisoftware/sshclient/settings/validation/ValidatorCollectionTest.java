package de.jowisoftware.sshclient.settings.validation;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.application.validation.ValidationResult;
import de.jowisoftware.sshclient.application.validation.Validator;
import de.jowisoftware.sshclient.application.validation.ValidatorCollection;
import de.jowisoftware.sshclient.settings.awt.AWTProfile;

@RunWith(JMock.class)
public class ValidatorCollectionTest {
    private final Mockery context = new JUnit4Mockery();

    @SuppressWarnings("unchecked")
    @Test
    public void callsAllItemsInCollection() {
        final Validator<AWTProfile> v1 = context.mock(Validator.class, "v1");
        final Validator<AWTProfile> v2 = context.mock(Validator.class, "v2");
        final Validator<AWTProfile> v3 = context.mock(Validator.class, "v3");
        final ValidationResult result = context.mock(ValidationResult.class);
        final AWTProfile profile = new AWTProfile();

        final ValidatorCollection<AWTProfile> collection = new ValidatorCollection<AWTProfile>();
        collection.addValidator(v1);
        collection.addValidator(v2);
        collection.addValidator(v3);

        context.checking(new Expectations() {{
            oneOf(v1).validate(profile, result);
            oneOf(v2).validate(profile, result);
            oneOf(v3).validate(profile, result);
        }});

        collection.validate(profile, result);
    }
}
