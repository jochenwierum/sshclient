package de.jowisoftware.sshclient.application;


public interface PasswordManager {
    void init(Application application);

    void changeMasterPassword();
    String getPassword(String passwordId, boolean firstWasWrong)
            throws UserAbortException;
}
