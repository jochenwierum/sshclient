package de.jowisoftware.sshclient.application.arguments;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.application.ApplicationSettings;
import de.jowisoftware.sshclient.terminal.Profile;

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
        final List<String> errors = new LinkedList<String>();

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
                return parseDefaultArg(arg.substring(1));
            } else if (arg.startsWith("+")) {
                final String sessionName = arg.substring(1);
                final SessionConnectArgument<T> argument = new SessionConnectArgument<T>(sessionName);
                callbacks.openConnection(argument.getProfile(settings));
                return ArgumentResult.TAKE_ONE;
            } else {
                final FreeStyleConnectArgument<T> argument = new FreeStyleConnectArgument<T>(arg);
                callbacks.openConnection(argument.getProfile(settings));
                return ArgumentResult.TAKE_ONE;
            }
        } catch(final RuntimeException e) {
            LOGGER.warn("Problem while parsing command line argument '" +
                    arg + "'", e);
        }
        return ArgumentResult.ERROR;
    }

    private ArgumentResult parseDefaultArg(final String substring) {
        return ArgumentResult.ERROR;
    }
}
