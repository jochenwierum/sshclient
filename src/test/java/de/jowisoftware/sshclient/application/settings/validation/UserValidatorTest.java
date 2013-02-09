package de.jowisoftware.sshclient.application.settings.validation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class UserValidatorTest extends AbstractValidationTest<Profile<?>> {
    @BeforeMethod
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
