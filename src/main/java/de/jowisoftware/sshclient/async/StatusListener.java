package de.jowisoftware.sshclient.async;

public interface StatusListener {
    void beginAction(String text);
    void endAction(final String name);
}
