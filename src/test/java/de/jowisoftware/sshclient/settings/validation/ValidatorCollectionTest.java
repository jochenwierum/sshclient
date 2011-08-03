package de.jowisoftware.sshclient.settings.validation;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.settings.Profile;

@RunWith(JMock.class)
public class ValidatorCollectionTest {
    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testCallsAll() {
        final Validator v1 = context.mock(Validator.class, "v1");
        final Validator v2 = context.mock(Validator.class, "v2");
        final Validator v3 = context.mock(Validator.class, "v3");
        final ValidationResult result = context.mock(ValidationResult.class);
        final Profile profile = new Profile();

        final ValidatorCollection collection = new ValidatorCollection();
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
