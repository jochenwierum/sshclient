package de.jowisoftware.sshclient.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.encryption.CryptoException;
import de.jowisoftware.sshclient.encryption.PasswordManager;

public class ApplicationSettings {
    public enum TabState {
        OPENED, CLOSED, ALYWAYS_OPEN, ALWAYS_CLOSED
    }

    private static final Logger LOGGER = Logger
            .getLogger(ApplicationSettings.class);

    private final Map<String, AWTProfile> profiles = new HashMap<String, AWTProfile>();
    private final List<File> keyFiles = new ArrayList<File>();

    private final PasswordManager passwordManager;

    private TabState logTabState = TabState.CLOSED;
    private TabState keyTabState = TabState.CLOSED;
    private String language = "en_US";

    public ApplicationSettings() {
        PasswordManager newPasswordManager;
        try {
            newPasswordManager = new PasswordManager();
        } catch(final CryptoException e) {
            newPasswordManager = null;
            LOGGER.error("Could not initialize password manager backend; " +
            		"ignoring it completely", e);
        }
        this.passwordManager = newPasswordManager;
    }

    public Map<String, AWTProfile> getProfiles() {
        return profiles;
    }

    public List<File> getKeyFiles() {
        return keyFiles;
    }

    public TabState getLogTabState() {
        return logTabState;
    }

    public TabState getKeyTabState() {
        return keyTabState;
    }

    public void setKeyTabState(final TabState state) {
        keyTabState = state;
    }

    public void setLogTabState(final TabState state) {
        logTabState = state;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }
}
