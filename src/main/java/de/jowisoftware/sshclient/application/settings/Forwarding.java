package de.jowisoftware.sshclient.application.settings;

public class Forwarding {
    public final String sourceHost;
    public final String remoteHost;
    public final int sourcePort;
    public final int remotePort;

    public Forwarding(final String sourceHost, final int sourcePort, final String remoteHost,
            final int remotePort) {
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        this.remotePort = remotePort;
        this.remoteHost = remoteHost;
    }
}
