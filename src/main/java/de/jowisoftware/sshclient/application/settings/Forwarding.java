package de.jowisoftware.sshclient.application.settings;

import de.jowisoftware.sshclient.application.settings.persistence.annotations.Persist;

public class Forwarding {
    public static enum Direction {
        LOCAL("L"), REMOTE("R");

        public final String shortName;

        private Direction(final String shortName) {
            this.shortName = shortName;
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
        // this method is used via reflection to deserialize a forwarding
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

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Forwarding)) {
            return false;
        }

        Forwarding otherForwarding = (Forwarding) other;

        if (direction == null) {
            if (otherForwarding.direction != null) {
                return false;
            }
        } else if (!direction.equals(otherForwarding.direction)) {
            return false;
        }

        if (remoteHost == null) {
            if (otherForwarding.remoteHost != null) {
                return false;
            }
        } else if (!remoteHost.equals(otherForwarding.remoteHost)) {
            return false;
        }

        if (sourceHost == null) {
            if (otherForwarding.sourceHost != null) {
                return false;
            }
        } else if (!sourceHost.equals(otherForwarding.sourceHost)) {
            return false;
        }

        if (remotePort != otherForwarding.remotePort) {
            return false;
        }

        return sourcePort == otherForwarding.sourcePort;
    }
}
