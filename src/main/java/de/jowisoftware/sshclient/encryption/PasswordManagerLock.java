package de.jowisoftware.sshclient.encryption;

public class PasswordManagerLock {
    private final String checkString;
    private boolean locked = true;
    private final EnDeCryptor cryptor;

    public PasswordManagerLock(final String checkString, final EnDeCryptor cryptor) {
        this.checkString = checkString;
        this.cryptor = cryptor;
    }

    public void unlock() throws CryptoException {
        try {
            tryUnlock();
        } catch(final NumberFormatException e) {
            processUnlockException(e);
        } catch(final CryptoException e) {
            processUnlockException(e);
        }
    }

    private void processUnlockException(final Exception e) throws CryptoException {
        throw new CryptoException("could not unlock password storage - illegal password?", e);
    }

    private void tryUnlock() throws CryptoException {
        locked = true;
        final String result = cryptor.decrypt(checkString);

        if (Integer.parseInt(result) % 23 == 0) {
            locked = false;
        } else {
            throw new CryptoException("Safety check failed");
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }
}
