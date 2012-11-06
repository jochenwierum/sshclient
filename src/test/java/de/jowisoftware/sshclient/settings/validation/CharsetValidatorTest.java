package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.validation.CharsetValidator;
import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.settings.awt.AWTProfile;

public class CharsetValidatorTest extends ValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new CharsetValidator();
    }

    @Test
    public void testNullCharset() {
        profile.setCharsetName(null);
        assertError("charset", "no charset selected");
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
