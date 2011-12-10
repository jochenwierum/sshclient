package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.validation.TimeoutValidator;
import de.jowisoftware.sshclient.terminal.Profile;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;

public class TimeoutValidatorTest extends ValidationTest<Profile<?>> {
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
