package de.jowisoftware.sshclient.application.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class KeepAliveCountValidatorTest extends AbstractValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new KeepAliveCountValidator();
    }

    @Test
    public void testInvalidValues() {
        profile.setKeepAliveCount(-1);
        final String message = "keep alive count is not in valid range (>= 0)";
        assertError("keepAliveCount", message);
        profile.setKeepAliveCount(-5);
        assertError("keepAliveCount", message);
    }

    @Test
    public void testValidValues() {
        profile.setKeepAliveCount(0);
        assertNoError();
        profile.setTimeout(10);
        assertNoError();
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
