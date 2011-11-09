package de.jowisoftware.sshclient.debug;

public class SystemTimeSource implements TimeSource {
    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }
}
