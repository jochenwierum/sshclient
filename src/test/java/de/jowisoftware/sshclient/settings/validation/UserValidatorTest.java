package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.terminal.Profile;

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
