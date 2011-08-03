package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

public class PortValidatorTest extends ValidationTest {
    @Before
    public void setUp() {
        validator = new PortValidator();
    }

    @Test
    public void testTooSmallPort() {
        final String expected = "illegal port";

        profile.setPort(-1);
        assertError("port", expected);
        profile.setPort(0);
        assertError("port", expected);
    }

    @Test
    public void testValidPort() {
        profile.setPort(1);
        assertNoError();

        profile.setPort(22);
        assertNoError();

        profile.setPort(2231);
        assertNoError();

        profile.setPort(Short.MAX_VALUE);
        assertNoError();
    }

    @Test
    public void testTooBigPort() {
        final String expected = "illegal port";

        profile.setPort(Short.MAX_VALUE + 1);
        assertError("port", expected);
        profile.setPort(Integer.MAX_VALUE - 2);
        assertError("port", expected);
    }
}
