package de.jowisoftware.sshclient.events;

public interface EventHub<T> extends EventHubClient<T> {
    T fire();
}
