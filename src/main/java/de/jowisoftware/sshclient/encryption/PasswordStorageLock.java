package de.jowisoftware.sshclient.encryption;

import java.security.SecureRandom;

public class PasswordStorageLock {
    private String checkString = null;
    private boolean locked = true;
    private final EnDeCryptor cryptor;

    public PasswordStorageLock(final EnDeCryptor cryptor) {
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

    public void setCheckString(final String checkString) {
        this.checkString = checkString;
        locked = true;
    }

    private void processUnlockException(final Exception e) throws CryptoException {
        throw new WrongPasswordException("could not unlock password storage - illegal password?", e);
    }

    private void tryUnlock() throws CryptoException {
        if (checkString == null) {
            throw new CryptoException("Checkstring not initialized");
        }

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

    public String getCheckString() {
        return checkString;
    }

    public void createCheckString() throws CryptoException {
        final int checkNumber = new SecureRandom().nextInt(Integer.MAX_VALUE / 23) * 23;
        checkString = cryptor.encrypt(Integer.toString(checkNumber));
    }
}
