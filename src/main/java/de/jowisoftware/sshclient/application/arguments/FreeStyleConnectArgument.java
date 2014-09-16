package de.jowisoftware.sshclient.application.arguments;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.Profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreeStyleConnectArgument<T extends Profile<?>> implements ConnectArgument<T> {
    private static final Pattern ARGUMENT_PATTERN =
            Pattern.compile("^(?:(?:([^:@]+)(?::([^@]+))?)@)?([^:]+)?(?::(\\d+))?$");
    private final String argument;

    public FreeStyleConnectArgument(final String argument) {
        this.argument = argument;
    }

    @Override
    public T getProfile(final ApplicationSettings<T> settings) {
        final Matcher matcher = ARGUMENT_PATTERN.matcher(argument);

        if (!matcher.find()) {
            throw new InvalidPatternException(argument);
        }

        final String user = matcher.group(1);
        //final String password = matcher.group(2);
        final String host = matcher.group(3);
        final String port = matcher.group(4);

        final T profile = settings.newDefaultProfile();
        profile.setHost(host);

        if (user != null) {
            profile.setUser(user);
        }

        if (port != null) {
            profile.setPort(Integer.parseInt(port));
        }

        return profile;
    }
}
