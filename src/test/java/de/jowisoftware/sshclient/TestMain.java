package de.jowisoftware.sshclient;

import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.persistence.Persister;

public class TestMain {
    public static void main(final String[] args) throws Exception {
        final AWTProfile awtProfile = new AWTProfile();

        awtProfile.getPortForwardings().add(new Forwarding(Forwarding.Direction.Local, "a", 80, "localhost", 80));
        awtProfile.getPortForwardings().add(new Forwarding(Forwarding.Direction.Remote, "b", 90, "127.0.0.1", 90));

        awtProfile.setCommand("myCommand");

        awtProfile.getEnvironment().put("x", "y");
        awtProfile.getEnvironment().put("a", "b");

        final AWTProfile awtProfile2 = new AWTProfile();

        String output = new Persister().persist(awtProfile);
        System.out.println(output);
        new Persister().restore(output, awtProfile2);

        System.out.println(new Persister().persist(awtProfile2));
    }
}
