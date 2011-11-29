package de.jowisoftware.sshclient.encryption;


public class CryptoException extends Exception {
    private static final long serialVersionUID = 3532040753519526260L;

    public CryptoException(final String message) {
        super(message);
    }

    public CryptoException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
