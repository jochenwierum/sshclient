package de.jowisoftware.sshclient.encryption;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PasswordStorage {
    public enum State {
        UNINITIALIZED, LOCKED, UNLOCKED
    }

    private final EnDeCryptor cryptor;
    private final PasswordStorageLock lock;
    private final Map<String, String> passwords = new HashMap<String, String>();

    PasswordStorage(final EnDeCryptor cryptor) {
        this.cryptor = cryptor;
        lock = new PasswordStorageLock(cryptor);
    }

    public PasswordStorage() throws CryptoException {
        this(new JavaStandardEnDeCryptor());
    }

    public void unlock(final String password) throws CryptoException {
        cryptor.setPassword(password);
        lock.unlock();
    }

    public void savePassword(final String passwordId, final String password) throws CryptoException {
        checkLock();
        passwords.put(passwordId, cryptor.encrypt(password));
    }

    public String restorePassword(final String passwordId) throws CryptoException {
        checkLock();

        final String cryptedPassword = passwords.get(passwordId);
        if (cryptedPassword == null) {
            return null;
        }

        return cryptor.decrypt(cryptedPassword);
    }

    private void checkLock() throws CryptoException {
        if (lock.isLocked()) {
            throw new StorageLockedException("Storage is locked");
        }
    }

    public void lock() {
        lock.lock();
    }

    public Map<String, String> exportPasswords() {
        return new HashMap<String, String>(passwords);
    }

    public void deletePassword(final String passwordId) throws CryptoException {
        checkLock();
        passwords.remove(passwordId);
    }

    public void importPasswords(final Map<String, String> additionalPasswords) {
        passwords.putAll(additionalPasswords);
    }

    public void changePassword(final String newPassword) throws CryptoException {
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

    public void setCheckString(final String checkString) {
        lock.setCheckString(checkString);
    }

    public String getCheckString() {
        return lock.getCheckString();
    }

    public State getState() {
        if (lock.getCheckString() == null) {
            return State.UNINITIALIZED;
        } else if (lock.isLocked()) {
            return State.LOCKED;
        } else {
            return State.UNLOCKED;
        }
    }

    public boolean hasPassword(final String passwordId) {
        return passwords.containsKey(passwordId);
    }

    public String[] exportPasswordIds() {
        return passwords.keySet().toArray(new String[passwords.size()]);
    }
}
