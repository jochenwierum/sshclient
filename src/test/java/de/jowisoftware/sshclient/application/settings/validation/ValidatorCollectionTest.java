package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class ValidatorCollectionTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @SuppressWarnings("unchecked")
    @Test
    public void callsAllItemsInCollection() {
        final Validator<AWTProfile> v1 = context.mock(Validator.class, "v1");
        final Validator<AWTProfile> v2 = context.mock(Validator.class, "v2");
        final Validator<AWTProfile> v3 = context.mock(Validator.class, "v3");
        final ValidationResult result = context.mock(ValidationResult.class);
        final AWTProfile profile = new AWTProfile();

        final ValidatorCollection<AWTProfile> collection = new ValidatorCollection<>();
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
