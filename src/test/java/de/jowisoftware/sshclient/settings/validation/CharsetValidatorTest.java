package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

public class CharsetValidatorTest extends ValidationTest {
    @Before
    public void setUp() {
        validator = new CharsetValidator();
    }

    @Test
    public void testNullCharset() {
        profile.setCharset(null);
        assertError("charset", "no charset selected");
    }
}
