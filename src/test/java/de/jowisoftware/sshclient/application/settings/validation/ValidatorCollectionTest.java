package de.jowisoftware.sshclient.application.settings.validation;

import org.jmock.Expectations;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.JMockTest;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class ValidatorCollectionTest extends JMockTest {
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
