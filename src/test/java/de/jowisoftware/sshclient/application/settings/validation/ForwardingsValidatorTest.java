package de.jowisoftware.sshclient.application.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class ForwardingsValidatorTest extends ValidationTest<Profile<?>> {
    @Before
    public void setUp() {
        validator = new ForwardingsValidator();
    }

    @Test public void
    emptyForwardingsAreAllowed() {
        assertNoError();
    }

    @Test public void
    validLocalForwardingsAreAllowed() {
        profile.getLocalForwardings().add(new Forwarding("localhost", 80, "remotehost", 80));
        profile.getLocalForwardings().add(new Forwarding("localhost2", 443, "remotehost2", 443));
        profile.getLocalForwardings().add(new Forwarding("localhost2", 1, "remotehost2", 1));

        assertNoError();
    }

    @Test public void
    validRemoteForwardingsAreAllowed() {
        profile.getRemoteForwardings().add(new Forwarding("localhost", 80, "remotehost", 80));
        profile.getRemoteForwardings().add(new Forwarding("localhost2", 443, "remotehost2", 443));
        profile.getRemoteForwardings().add(new Forwarding("localhost2", 1, "remotehost2", 1));

        assertNoError();
    }

    @Test public void
    invalidLocalPortInLocalForwardingsCauseErrors() {
        profile.getLocalForwardings().add(new Forwarding("localhost", 80, "remotehost", 80));
        profile.getLocalForwardings().add(new Forwarding("localhost2", 443, "remotehost2", -2));
        profile.getLocalForwardings().add(new Forwarding("localhost2", 443, "remotehost2", 0));

        assertError("localForwardings.1", "error.port.remotePort");
        assertError("localForwardings.2", "error.port.remotePort");
    }

    @Test public void
    invalidRemotePortInLocalForwardingsCauseErrors() {
        profile.getLocalForwardings().add(new Forwarding("localhost", 0, "remotehost", 2));
        profile.getLocalForwardings().add(new Forwarding("localhost2", 20, "remotehost2", 2));
        profile.getLocalForwardings().add(new Forwarding("localhost2", -20, "remotehost2", 2));

        assertError("localForwardings.0", "error.port.localPort");
        assertError("localForwardings.2", "error.port.localPort");
    }

    @Test public void
    invalidLocalPortInRemoteForwardingsCauseErrors() {
        profile.getRemoteForwardings().add(new Forwarding("localhost", 80, "remotehost", 80));
        profile.getRemoteForwardings().add(new Forwarding("localhost2", 443, "remotehost2", -2));
        profile.getRemoteForwardings().add(new Forwarding("localhost2", 443, "remotehost2", 0));

        assertError("remoteForwardings.1", "error.port.remotePort");
        assertError("remoteForwardings.2", "error.port.remotePort");
    }

    @Test public void
    invalidRemotePortInRemoteForwardingsCauseErrors() {
        profile.getRemoteForwardings().add(new Forwarding("localhost", 80, "remotehost", 82));
        profile.getRemoteForwardings().add(new Forwarding("localhost2", 0, "remotehost2", 2));
        profile.getRemoteForwardings().add(new Forwarding("localhost2", -100, "remotehost2", 2));

        assertError("remoteForwardings.1", "error.port.localPort");
        assertError("remoteForwardings.2", "error.port.localPort");
    }

    @Override
    protected Profile<?> newProfile() {
        return new AWTProfile();
    }
}
