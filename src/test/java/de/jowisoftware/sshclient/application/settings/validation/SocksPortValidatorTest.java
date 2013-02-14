package de.jowisoftware.sshclient.application.settings.validation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class SocksPortValidatorTest extends AbstractValidationTest<Profile<?>> {
    @BeforeMethod
    public void setUp() {
        validator = new SocksPortValidator();
    }

    @Test
    public void testTooSmallPort() {
        final String expected = "illegal SOCKS 4/5 port";

        profile.setSocksPort(-1);
        assertError("socksport", expected);
        profile.setSocksPort(0);
        assertError("socksport", expected);
    }

    @Test
    public void testValidPort() {
        profile.setSocksPort(1);
        assertNoError();

        profile.setSocksPort(22);
        assertNoError();

        profile.setSocksPort(2231);
        assertNoError();

        profile.setSocksPort((int) Short.MAX_VALUE);
        assertNoError();

        profile.setSocksPort(null);
        assertNoError();
    }

    @Test
    public void testTooBigPort() {
        final String expected = "illegal SOCKS 4/5 port";

        profile.setSocksPort(Short.MAX_VALUE + 1);
        assertError("socksport", expected);
        profile.setSocksPort(Integer.MAX_VALUE - 2);
        assertError("socksport", expected);
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
