package de.jowisoftware.sshclient.application.arguments;

import de.jowisoftware.sshclient.settings.ApplicationSettings;
import de.jowisoftware.sshclient.settings.Profile;

public interface ConnectArgument<T extends Profile<?>> {
    T getProfile(ApplicationSettings<T> settings);
}
