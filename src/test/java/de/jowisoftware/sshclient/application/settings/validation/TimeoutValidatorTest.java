package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import org.junit.Before;
import org.junit.Test;

public class TimeoutValidatorTest extends AbstractValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new TimeoutValidator();
    }

    @Test
    public void testInvalidValues() {
        profile.setTimeout(99);
        final String message = "timeout is not in valid range (> 100)";
        assertError("timeout", message);
        profile.setTimeout(-5);
        assertError("timeout", message);
    }

    @Test
    public void testValidValues() {
        profile.setTimeout(100);
        assertNoError();
        profile.setTimeout(50000);
        assertNoError();
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
