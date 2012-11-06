package de.jowisoftware.sshclient.application.arguments;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.Profile;

public interface ConnectArgument<T extends Profile<?>> {
    T getProfile(ApplicationSettings<T> settings);
}
