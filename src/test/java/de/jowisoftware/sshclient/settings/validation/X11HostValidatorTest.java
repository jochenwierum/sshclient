package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.validation.X11HostValidator;
import de.jowisoftware.sshclient.terminal.Profile;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;

public class X11HostValidatorTest extends ValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new X11HostValidator();
    }

    @Test
    public void testEmptyHostname() {
        final String message = "host name is empty";
        profile.setHost(null);
        assertError("x11host", message);
        profile.setHost("");
        assertError("x11host", message);
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
        assertError("x11host", message);

        profile.setHost("also/invalid");
        assertError("x11host", message);
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
