package de.jowisoftware.sshclient.application.arguments;

public class UnknownProfileException extends RuntimeException {
    private static final long serialVersionUID = -6065359278416088995L;

    public UnknownProfileException(final String message) {
        super(message);
    }
}
