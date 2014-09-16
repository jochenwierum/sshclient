package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import org.junit.Before;
import org.junit.Test;

public class EnvironmentValidatorTest extends AbstractValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new EnvironmentValidator();
    }

    @Test
    public void setGoodValues() {
        profile.getEnvironment().put("TERM", "xterm");
        profile.getEnvironment().put("SHELL", "zsh");
        profile.getEnvironment().put("username2", "what's that?");

        assertNoError();
    }

    @Test
    public void setBadValues() {
        profile.getEnvironment().put("TERM?", "vt101");
        assertError("environment", "illegal environment variable: TERM?");

        profile.getEnvironment().put(".*", "test");
        assertError("environment", "illegal environment variable: .*");
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
