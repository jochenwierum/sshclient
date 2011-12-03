package de.jowisoftware.sshclient.encryption;

public class StorageLockedException extends CryptoException {
    private static final long serialVersionUID = -5801894369443820690L;

    public StorageLockedException(final String message) {
        super(message);
    }
}
