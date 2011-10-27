package de.jowisoftware.sshclient.events;

public interface EventHub<T> {
    void register(T listener);
    T fire();
}
