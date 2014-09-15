package de.jowisoftware.sshclient.application.arguments;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.Profile;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class ArgumentParser<T extends Profile<?>> {
    private static final Logger LOGGER = Logger.getLogger(ArgumentParser.class);

    private final ArgumentParserCallback<T> callbacks;
    private final ApplicationSettings<T> settings;

    private enum ArgumentResult {
        ERROR, TAKE_ONE, TAKE_TWO
    }

    public ArgumentParser(final ArgumentParserCallback<T> callbacks,
            final ApplicationSettings<T> settings) {
        this.callbacks = callbacks;
        this.settings = settings;
    }

    public void processArguments(final String[] args) {
        final List<String> errors = new LinkedList<>();

        for (int i = 0; i < args.length; ++i) {
            String secondArg = null;
            if (i < args.length - 1) {
                secondArg = args[i + 1];
            }

            switch(processArgument(args[i], secondArg)) {
            case ERROR:
                errors.add(args[i]);
                break;
            case TAKE_TWO:
                ++i;
                break;
            default:
            }
        }

        if (errors.size() > 0) {
            callbacks.reportArgumentError(errors.toArray(new String[errors.size()]));
        }
    }

    private ArgumentResult processArgument(final String arg, final String secondArg) {
        try {
            if (arg.startsWith("-")) {
                return parseDefaultArg(arg.substring(1), secondArg);
            } else if (arg.startsWith("+")) {
                return parseSession(arg);
            } else {
                return parseFreeStyle(arg);
            }
        } catch(final RuntimeException e) {
            LOGGER.warn("Problem while parsing command line argument '" +
                    arg + '\'', e);
        }
        return ArgumentResult.ERROR;
    }

    @SuppressWarnings("SameReturnValue")
    private ArgumentResult parseFreeStyle(final String arg) {
        final ConnectArgument<T> argument = new FreeStyleConnectArgument<>(arg);
        callbacks.openConnection(argument.getProfile(settings));
        return ArgumentResult.TAKE_ONE;
    }

    @SuppressWarnings("SameReturnValue")
    private ArgumentResult parseSession(final String arg) {
        final boolean isSftpSession;
        final String sessionName;

        if (arg.startsWith("++")) {
            isSftpSession = true;
            sessionName = arg.substring(2);
        } else {
            isSftpSession = false;
            sessionName = arg.substring(1);
        }

        final ConnectArgument<T> argument = new SessionConnectArgument<>(sessionName);

        if (isSftpSession) {
            callbacks.openSftpConnection(argument.getProfile(settings));
        } else {
            callbacks.openConnection(argument.getProfile(settings));
        }

        return ArgumentResult.TAKE_ONE;
    }

    private ArgumentResult parseDefaultArg(final String argument, final String parameter) {
        if (argument.toLowerCase().equals("key") && parameter != null) {
            callbacks.loadKey(parameter);
            return ArgumentResult.TAKE_TWO;
        } else {
            return ArgumentResult.ERROR;
        }
    }
}
