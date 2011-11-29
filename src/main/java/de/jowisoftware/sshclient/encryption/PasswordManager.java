package de.jowisoftware.sshclient.encryption;

import java.util.HashMap;
import java.util.Map;

public class PasswordManager {
    private final EnDeCryptor cryptor;
    private final PasswordManagerLock lock;
    private final Map<String, String> passwords = new HashMap<String, String>();

    PasswordManager(final EnDeCryptor cryptor, final String checkString) {
        this.cryptor = cryptor;
        lock = new PasswordManagerLock(checkString, cryptor);
    }

    public PasswordManager(final String checkString) throws CryptoException {
        this(new JavaStandardEnDeCryptor(), checkString);
    }

    public synchronized boolean isLocked() {
        return lock.isLocked();
    }

    public synchronized void unlock(final String password) throws CryptoException {
        cryptor.setPassword(password);
        lock.unlock();
    }

    public synchronized void savePassword(final String passwordId, final String password) throws CryptoException {
        checkLock();
        passwords.put(passwordId, cryptor.encrypt(password));
    }

    public synchronized String restorePassword(final String passwordId) throws CryptoException {
        final String cryptedPassword = passwords.get(passwordId);
        if (cryptedPassword == null) {
            return null;
        }

        checkLock();
        return cryptor.decrypt(cryptedPassword);
    }

    private void checkLock() throws CryptoException {
        if (isLocked()) {
            throw new CryptoException("Storage is locked");
        }
    }

    public synchronized void lock() {
        lock.lock();
    }

    public synchronized Map<String, String> exportPasswords() {
        return new HashMap<String, String>(passwords);
    }
}
