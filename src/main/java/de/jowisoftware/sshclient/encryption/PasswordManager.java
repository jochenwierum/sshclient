package de.jowisoftware.sshclient.encryption;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PasswordManager {
    public enum State {
        UNINITIALIZED, LOCKED, UNLOCKED
    }

    private final EnDeCryptor cryptor;
    private final PasswordManagerLock lock;
    private final Map<String, String> passwords = new HashMap<String, String>();

    PasswordManager(final EnDeCryptor cryptor) {
        this.cryptor = cryptor;
        lock = new PasswordManagerLock(cryptor);
    }

    public PasswordManager() throws CryptoException {
        this(new JavaStandardEnDeCryptor());
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
        if (lock.isLocked()) {
            throw new CryptoException("Storage is locked");
        }
    }

    public synchronized void lock() {
        lock.lock();
    }

    public synchronized Map<String, String> exportPasswords() {
        return new HashMap<String, String>(passwords);
    }

    public synchronized void deletePassword(final String passwordId) throws CryptoException {
        checkLock();
        passwords.remove(passwordId);
    }

    public synchronized void importPasswords(final Map<String, String> additionalPasswords) {
        passwords.putAll(additionalPasswords);
    }

    public synchronized void changePassword(final String newPassword) throws CryptoException {
        if (lock.getCheckString() != null) {
            checkLock();
        }

        final Map<String, String> temp = decryptAllPasswords();
        applyNewPassword(newPassword);
        encryptAllPasswords(temp);
    }

    private void applyNewPassword(final String newPassword)
            throws CryptoException {
        cryptor.setPassword(newPassword);
        lock.createCheckString();
        lock.unlock();
    }

    private void encryptAllPasswords(final Map<String, String> temp)
            throws CryptoException {
        for (final Entry<String, String> entry : temp.entrySet()) {
            entry.setValue(cryptor.encrypt(entry.getValue()));
        }

        passwords.putAll(temp);
    }

    private Map<String, String> decryptAllPasswords() throws CryptoException {
        final Map<String, String> temp = new HashMap<String, String>();

        for (final Entry<String, String> entry : passwords.entrySet()) {
            temp.put(entry.getKey(), cryptor.decrypt(entry.getValue()));
        }
        return temp;
    }

    public synchronized void setCheckString(final String checkString) {
        lock.setCheckString(checkString);
    }

    public synchronized String getCheckString() {
        return lock.getCheckString();
    }

    public synchronized State getState() {
        if (lock.getCheckString() == null) {
            return State.UNINITIALIZED;
        } else if (lock.isLocked()) {
            return State.LOCKED;
        } else {
            return State.UNLOCKED;
        }
    }
}
