package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

public class EnvironmentValidatorTest extends ValidationTest {
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
}
