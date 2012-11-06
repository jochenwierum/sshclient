package de.jowisoftware.sshclient.application.arguments;

import de.jowisoftware.sshclient.application.settings.Profile;

public interface ArgumentParserCallback<T extends Profile<?>> {
    void openConnection(T profile);
    void reportArgumentError(String[] errors);
    void loadKey(String path);
}
