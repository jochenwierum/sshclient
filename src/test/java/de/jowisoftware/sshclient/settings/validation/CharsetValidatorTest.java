package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.terminal.Profile;

public class CharsetValidatorTest extends ValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new CharsetValidator();
    }

    @Test
    public void testNullCharset() {
        profile.setCharset(null);
        assertError("charset", "no charset selected");
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
