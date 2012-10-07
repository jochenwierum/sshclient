package de.jowisoftware.sshclient.application.arguments;

import de.jowisoftware.sshclient.application.ApplicationSettings;
import de.jowisoftware.sshclient.terminal.Profile;

public interface ConnectArgument<T extends Profile<?>> {
    T getProfile(ApplicationSettings<T> settings);
}
