package de.jowisoftware.sshclient.application.arguments;

public class InvalidPatternException extends RuntimeException {
    private static final long serialVersionUID = 3202507990294539006L;

    public InvalidPatternException(final String message) {
        super(message);
    }
}
