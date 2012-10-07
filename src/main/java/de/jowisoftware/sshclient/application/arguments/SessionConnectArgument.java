package de.jowisoftware.sshclient.application.arguments;

import java.util.Map;

import de.jowisoftware.sshclient.application.ApplicationSettings;
import de.jowisoftware.sshclient.terminal.Profile;

public class SessionConnectArgument<T extends Profile<?>> implements ConnectArgument<T> {
    private final String sessionName;

    public SessionConnectArgument(final String sessionName) {
        this.sessionName = sessionName.toLowerCase();
    }

    @Override
    public T getProfile(final ApplicationSettings<T> settings) {
        for (final Map.Entry<String, T> profile :
                settings.getProfiles().entrySet()) {
            if (profile.getKey().toLowerCase().equals(sessionName)) {
                return profile.getValue();
            }
        }

        throw new UnknownProfileException(sessionName);
    }
}
