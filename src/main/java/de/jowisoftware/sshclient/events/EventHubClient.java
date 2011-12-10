package de.jowisoftware.sshclient.events;

public interface EventHubClient<T> {
    void register(T listener);
}
