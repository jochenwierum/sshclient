package de.jowisoftware.sshclient.encryption;

import de.jowisoftware.sshclient.application.settings.persistence.annotations.Persist;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.PersistPostLoad;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.PersistPreSave;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.TraversalType;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PasswordStorage {
    public enum State {
        Uninitialized, Locked, Unlocked
    }

    private final EnDeCryptor cryptor;
    private final PasswordStorageLock lock;

    @Persist(value = "passwords", traversalType = TraversalType.Map, targetClass = String.class, targetClass2 = String.class)
    private final Map<String, String> passwords = new HashMap<>();

    @Persist("@check")
    private String checkString;

    PasswordStorage(final EnDeCryptor cryptor) {
        this.cryptor = cryptor;
        lock = new PasswordStorageLock(cryptor);
    }

    public PasswordStorage() throws CryptoException {
        this(new JavaStandardEnDeCryptor());
    }

    @PersistPostLoad
    public void prepareLoad() {
        if (checkString != null && checkString.isEmpty()) {
            checkString = null;
        }
        lock.setCheckString(checkString);
    }

    @PersistPreSave
    public void prepareSave() {
        checkString = lock.getCheckString();
        if (checkString == null) {
            checkString = "";
        }
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

    public void deletePassword(final String passwordId) throws CryptoException {
        checkLock();
        passwords.remove(passwordId);
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
        checkString = lock.getCheckString();
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
        final Map<String, String> temp = new HashMap<>();

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
            return State.Uninitialized;
        } else if (lock.isLocked()) {
            return State.Locked;
        } else {
            return State.Unlocked;
        }
    }

    public boolean hasPassword(final String passwordId) {
        return passwords.containsKey(passwordId);
    }

    public String[] exportPasswordIds() {
        return passwords.keySet().toArray(new String[passwords.size()]);
    }

    public Map<String, String> getPasswordMap() {
        return passwords;
    }
}
