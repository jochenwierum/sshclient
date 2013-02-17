package de.jowisoftware.sshclient.application.settings;

import de.jowisoftware.sshclient.application.settings.persistence.annotations.Persist;

public class Forwarding {
    public static enum Direction {
        LOCAL("L", "local"), REMOTE("R", "remote");

        public final String shortName;
        public final String longName;

        private Direction(final String shortName, final String longName) {
            this.shortName = shortName;
            this.longName = longName;
        }
    }

    @Persist("@direction")
    private Direction direction;

    @Persist("@sourceHost")
    private String sourceHost;

    @Persist("@remoteHost")
    private String remoteHost;

    @Persist("@sourcePort")
    private int sourcePort;

    @Persist("@remotePort")
    private int remotePort;

    public Forwarding(
            final Direction direction,
            final String sourceHost, final int sourcePort, final String remoteHost,
            final int remotePort) {
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        this.remotePort = remotePort;
        this.remoteHost = remoteHost;
        this.direction = direction;
    }

    public Forwarding() {
    }

    public Direction getDirection() {
        return direction;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public String toString() {
        return direction.shortName + ": " + sourceHost + ":" + sourcePort
                + " to " + remoteHost + ":" + remotePort;
    }
}
