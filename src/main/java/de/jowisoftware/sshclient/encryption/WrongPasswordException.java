package de.jowisoftware.sshclient.encryption;

public class WrongPasswordException extends CryptoException {
    private static final long serialVersionUID = -1205885008514441683L;

    public WrongPasswordException(final String message) {
        super(message);
    }
}
