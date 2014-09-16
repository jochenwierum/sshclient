package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.Forwarding.Direction;
import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import org.junit.Before;
import org.junit.Test;

public class ForwardingsValidatorTest extends AbstractValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new ForwardingsValidator();
    }

    @Test public void
    emptyForwardingsAreAllowed() {
        assertNoError();
    }

    @Test public void
    validForwardingsAreAllowed() {
        profile.getPortForwardings().add(new Forwarding(Direction.LOCAL, "localhost", 80, "remotehost", 80));
        profile.getPortForwardings().add(new Forwarding(Direction.REMOTE, "localhost2", 443, "remotehost2", 443));
        profile.getPortForwardings().add(new Forwarding(Direction.LOCAL, "localhost2", 1, "remotehost2", 1));

        assertNoError();
    }

    @Test public void
    invalidLocalPortInCauseErrors() {
        profile.getPortForwardings().add(new Forwarding(Direction.LOCAL, "localhost", 80, "remotehost", 80));
        profile.getPortForwardings().add(new Forwarding(Direction.LOCAL, "localhost2", 443, "remotehost2", -2));
        profile.getPortForwardings().add(new Forwarding(Direction.LOCAL, "localhost2", 443, "remotehost2", 0));

        assertError("portForwardings.1", "error.port.remotePort");
        assertError("portForwardings.2", "error.port.remotePort");
    }

    @Test public void
    invalidRemotePortCauseErrors() {
        profile.getPortForwardings().add(new Forwarding(Direction.LOCAL, "localhost", 0, "remotehost", 2));
        profile.getPortForwardings().add(new Forwarding(Direction.LOCAL, "localhost2", 20, "remotehost2", 2));
        profile.getPortForwardings().add(new Forwarding(Direction.LOCAL, "localhost2", -20, "remotehost2", 2));

        assertError("portForwardings.0", "error.port.localPort");
        assertError("portForwardings.2", "error.port.localPort");
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
