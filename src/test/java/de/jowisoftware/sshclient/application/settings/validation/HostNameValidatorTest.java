package de.jowisoftware.sshclient.application.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.HostNameValidator;

public class HostNameValidatorTest extends ValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new HostNameValidator();
    }

    @Test
    public void testEmptyHostname() {
        final String message = "host name is empty";
        profile.setHost(null);
        assertError("host", message);
        profile.setHost("");
        assertError("host", message);
    }

    @Test
    public void testValidHostnames() {
        profile.setHost("191.168.0.1");
        assertNoError();

        profile.setHost("my.host.com");
        assertNoError();

        profile.setHost("[::1]");
        assertNoError();
    }

    @Test
    public void testInvalidValidHostnames() {
        final String message = "host format is not valid";
        profile.setHost("user@host");
        assertError("host", message);

        profile.setHost("also/invalid");
        assertError("host", message);
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
