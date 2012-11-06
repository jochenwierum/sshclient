package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.validation.UserValidator;
import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.settings.awt.AWTProfile;

public class UserValidatorTest extends ValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new UserValidator();
    }

    @Test
    public void testNoUser() {
        final String message = "user name is missing";
        profile.setUser(null);
        assertError("user", message);
        profile.setUser("");
        assertError("user", message);
    }

    @Test
    public void testValidUsername() {
        profile.setUser("root");
        assertNoError();

        profile.setUser("myUser");
        assertNoError();
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
