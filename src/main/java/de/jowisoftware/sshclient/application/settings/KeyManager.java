package de.jowisoftware.sshclient.application.settings;

import de.jowisoftware.sshclient.events.EventHubClient;

public interface KeyManager {
    void loadKey(String absolutePath);
    void removeIdentity(String name);

    void loadKeyListFromSettings();
    void persistKeyListToSettings();

    EventHubClient<KeyManagerEvents> eventListeners();
}