package de.jowisoftware.sshclient.application.settings.validation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.Forwarding.Direction;
import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class ForwardingsValidatorTest extends AbstractValidationTest<Profile<?>> {
    @BeforeMethod
    public void setUp() {
        validator = new ForwardingsValidator();
    }

    @Test public void
    emptyForwardingsAreAllowed() {
        assertNoError();
    }

    @Test public void
    validForwardingsAreAllowed() {
        profile.getPortForwardings().add(new Forwarding(Direction.Local, "localhost", 80, "remotehost", 80));
        profile.getPortForwardings().add(new Forwarding(Direction.Remote, "localhost2", 443, "remotehost2", 443));
        profile.getPortForwardings().add(new Forwarding(Direction.Local, "localhost2", 1, "remotehost2", 1));

        assertNoError();
    }

    @Test public void
    invalidLocalPortInCauseErrors() {
        profile.getPortForwardings().add(new Forwarding(Direction.Local, "localhost", 80, "remotehost", 80));
        profile.getPortForwardings().add(new Forwarding(Direction.Local, "localhost2", 443, "remotehost2", -2));
        profile.getPortForwardings().add(new Forwarding(Direction.Local, "localhost2", 443, "remotehost2", 0));

        assertError("portForwardings.1", "error.port.remotePort");
        assertError("portForwardings.2", "error.port.remotePort");
    }

    @Test public void
    invalidRemotePortCauseErrors() {
        profile.getPortForwardings().add(new Forwarding(Direction.Local, "localhost", 0, "remotehost", 2));
        profile.getPortForwardings().add(new Forwarding(Direction.Local, "localhost2", 20, "remotehost2", 2));
        profile.getPortForwardings().add(new Forwarding(Direction.Local, "localhost2", -20, "remotehost2", 2));

        assertError("portForwardings.0", "error.port.localPort");
        assertError("portForwardings.2", "error.port.localPort");
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
