package de.jowisoftware.sshclient.application.settings;

public class Forwarding {
    public static enum Direction {
        Local("L", "local"), Remote("R", "remote");

        public final String shortName;
        public final String longName;

        private Direction(final String shortName, final String longName) {
            this.shortName = shortName;
            this.longName = longName;
        }
    }

    public final Direction direction;
    public final String sourceHost;
    public final String remoteHost;
    public final int sourcePort;
    public final int remotePort;

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

    @Override
    public String toString() {
        return direction.shortName + ": " + sourceHost + ":" + sourcePort
                + " to " + remoteHost + ":" + remotePort;
    }
}
