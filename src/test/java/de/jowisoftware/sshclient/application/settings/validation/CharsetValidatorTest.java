package de.jowisoftware.sshclient.application.settings.validation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class CharsetValidatorTest extends AbstractValidationTest<Profile<?>> {
    @BeforeMethod
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
