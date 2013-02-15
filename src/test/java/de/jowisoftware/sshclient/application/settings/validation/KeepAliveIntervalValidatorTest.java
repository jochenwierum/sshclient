package de.jowisoftware.sshclient.application.settings.validation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class KeepAliveIntervalValidatorTest extends AbstractValidationTest<Profile<?>> {
    @BeforeMethod
    public void setUp() {
        validator = new KeepAliveIntervalValidator();
    }

    @Test
    public void testInvalidValues() {
        profile.setKeepAliveInterval(99);
        final String message = "keep alive interval is not in valid range (> 100)";
        assertError("keepAliveInterval", message);
        profile.setKeepAliveInterval(-5);
        assertError("keepAliveInterval", message);
    }

    @Test
    public void testValidValues() {
        profile.setKeepAliveInterval(100);
        assertNoError();
        profile.setKeepAliveInterval(50000);
        assertNoError();
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
