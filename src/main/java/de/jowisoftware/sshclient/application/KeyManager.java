package de.jowisoftware.sshclient.application;

import de.jowisoftware.sshclient.events.EventHubClient;

public interface KeyManager {
    void loadKey(String absolutePath, String password);
    void removeIdentity(String name);

    void loadKeyListFromSettings();
    void persistKeyListToSettings();

    EventHubClient<KeyManagerEvents> eventListeners();
}