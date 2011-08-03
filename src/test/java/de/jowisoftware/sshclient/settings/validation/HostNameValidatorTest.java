package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

public class HostNameValidatorTest extends ValidationTest {
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
}
