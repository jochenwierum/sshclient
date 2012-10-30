package de.jowisoftware.sshclient.ui.security;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.PasswordManager;
import de.jowisoftware.sshclient.application.UserAbortException;
import de.jowisoftware.sshclient.encryption.CryptoException;
import de.jowisoftware.sshclient.encryption.PasswordStorage;
import de.jowisoftware.sshclient.encryption.PasswordStorage.State;
import de.jowisoftware.sshclient.encryption.WrongPasswordException;
import de.jowisoftware.sshclient.util.SwingUtils;

public class SimplePasswordManager implements PasswordManager {
    private static final Logger LOGGER = Logger
            .getLogger(SimplePasswordManager.class);

    private PasswordStorage storage;
    private Application application;

    @Override
    public void init(final Application newApplication) {
        this.application = newApplication;
        storage = application.settings.getPasswordStorage();
    }

    @Override
    public String getPassword(final String passwordId, final boolean firstWasWrong)
            throws UserAbortException {

        String password = null;

        if (storage.hasPassword(passwordId) && !firstWasWrong) {
            password = readPasswordFromStorage(passwordId);
        }

        if (password == null) {
            final PasswordData passwordData = readPasswordFromUser(passwordId, true);
            savePassword(passwordId, passwordData);
            password = passwordData.password;
        }

        return password;
    }

    private void savePassword(final String passwordId,
            final PasswordData passwordData) {
        if (!passwordData.save) {
            return;
        }

        try {
            unlockStorage();
            storage.savePassword(passwordId, passwordData.password);
        } catch (final CryptoException e) {
            showMessage(t("security.save.exception",
                    "Could not add password to password manager."));
            LOGGER.error("Could not save new password in password manager", e);
        } catch (final UserAbortException e) {
            LOGGER.info("User did cancel request to open password storage, ignoring save request", e);
        }
    }

    private PasswordData readPasswordFromUser(final String passwordId,
            final boolean allowSaving) throws UserAbortException {
        final PasswordRequestingSwingRunnable runnable =
                new PasswordRequestingSwingRunnable(
                        createMessage(passwordId),
                        allowSaving, application.mainWindow);
        return runnable.askUser();
    }

    private String createMessage(final String passwordId) {
        return t("security.require_password", "Password for '%s' required", passwordId);
    }

    private String readPasswordFromStorage(final String passwordId) {
        if (!storage.hasPassword(passwordId)) {
            return null;
        }

        try {
            unlockStorage();
            return storage.restorePassword(passwordId);
        } catch (final CryptoException e) {
            LOGGER.error("Could not restore password, keeping storage locked", e);
            return null;
        } catch (final UserAbortException e) {
            LOGGER.info("User did cancel request to open password storage, keeping storage locked");
            return null;
        }
    }

    private void unlockStorage() throws UserAbortException {
        if (storage.getState() == State.LOCKED) {
            openStorage(t("security.master.key", "Master"));
        } else if(storage.getState() == State.UNINITIALIZED) {
            updateStoragePassword();
        }
    }

    private void updateStoragePassword() throws UserAbortException {
        final String keyName1 = t("security.master.key.new", "Master (new)");
        final String keyName2 = t("security.master.key.repeat", "Master (repeat)");
        String pw1 = null;
        String pw2 = null;

        while (pw1 == null || !pw1.equals(pw2)) {
            pw1 = readPasswordFromUser(keyName1, false).password;
            pw2 = readPasswordFromUser(keyName2, false).password;
        }

        try {
            storage.changePassword(pw1);
        } catch (final CryptoException e) {
            showMessage(t("security.exception.change",
                    "Could not change storage password. Was " +
                    "the password manager created with a stronger " +
                    "encryption than this version of java supports " +
                    "or is the password Manager broken?"));
            LOGGER.error("Could not chagne storage password", e);
            throw new RuntimeException(e);
        }
    }

    private void openStorage(final String keyName) throws UserAbortException {
        while(storage.getState() == State.LOCKED) {
            try {
                final String password = readPasswordFromUser(keyName, false).password;
                storage.unlock(password);
            } catch (final WrongPasswordException e) {
                // ignored, just retry with new password
            } catch (final CryptoException e) {
                showMessage(t("security.exception.unlock",
                        "Could not unlock password manager. Was " +
                        "the password manager created with a stronger " +
                        "encryption than this version of java supports " +
                        "or is the password Manager broken?"));
                LOGGER.error("Could not unlock password storage", e);
                throw new RuntimeException(e);
            }
        }
    }

    private void showMessage(final String message) {
        SwingUtils.showMessage(application.mainWindow, message, "SSH", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void changeMasterPassword() {
        try {
            if (storage.getState() == State.LOCKED) {
                openStorage(t("security.master.key.old", "Master (old)"));
            }
            updateStoragePassword();
        } catch(final UserAbortException e) {
            // do nothing, simply do not change the password
        }
    }

    @Override
    public void deletePassword(final String passwordId) {
        try {
            unlockStorage();
            storage.deletePassword(passwordId);
        } catch (final CryptoException e) {
            LOGGER.error("Unable to delete password '" + passwordId + "'", e);
            showMessage(t("security.exception.delete",
                    "Unable to delete password '%s'.", passwordId));
        } catch (final UserAbortException e) {
            // User aborts delete process
        }
    }

    @Override
    public SortedMap<String, String> getPasswords(final boolean showPasswords) {
        if (showPasswords) {
            try {
                unlockStorage();
            } catch (final UserAbortException e) {
                // keep storage locked
            }
        }

        final SortedMap<String, String> result = new TreeMap<String, String>();
        for (final String key : storage.exportPasswordIds()) {
            String password = "";
            if (showPasswords && storage.getState() == PasswordStorage.State.UNLOCKED) {
                try {
                    password = storage.restorePassword(key);
                } catch (final CryptoException e) {
                    password = "defect";
                }
            }
            result.put(key, password);
        }
        return result;
    }
}
