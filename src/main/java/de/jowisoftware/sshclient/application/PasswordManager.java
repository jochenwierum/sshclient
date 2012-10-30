package de.jowisoftware.sshclient.application;

import java.util.SortedMap;

public interface PasswordManager {
    void init(Application application);

    void changeMasterPassword();

    String getPassword(String passwordId, boolean firstWasWrong)
            throws UserAbortException;
    SortedMap<String, String> getPasswords(boolean showPasswords);

    void deletePassword(String passwordId);
}
