package de.jowisoftware.sshclient.application.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class X11DisplayValidatorTest extends AbstractValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new X11DisplayValidator();
    }

    @Test
    public void testTooSmallPort() {
        final String expected = "illegal X11 display";

        profile.setPort(-1);
        assertError("x11display", expected);
        profile.setPort(-10);
        assertError("x11display", expected);
    }

    @Test
    public void testValidPort() {
        profile.setPort(0);
        assertNoError();

        profile.setPort(22);
        assertNoError();

        profile.setPort(Short.MAX_VALUE);
        assertNoError();
    }

    @Test
    public void testTooBigPort() {
        final String expected = "illegal X11 display";

        profile.setPort(Short.MAX_VALUE + 1);
        assertError("x11display", expected);
        profile.setPort(Integer.MAX_VALUE - 2);
        assertError("x11display", expected);
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
