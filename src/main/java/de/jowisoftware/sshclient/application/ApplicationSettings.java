package de.jowisoftware.sshclient.application;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.encryption.CryptoException;
import de.jowisoftware.sshclient.encryption.PasswordStorage;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;

public class ApplicationSettings {
    public enum TabState {
        OPENED, CLOSED, ALYWAYS_OPEN, ALWAYS_CLOSED
    }

    private static final Logger LOGGER = Logger
            .getLogger(ApplicationSettings.class);

    private final Map<String, AWTProfile> profiles = new HashMap<String, AWTProfile>();
    private final List<File> keyFiles = new ArrayList<File>();

    private final PasswordStorage passwordStorage;
    private boolean unlockKeysOnStartup = false;

    private TabState logTabState = TabState.CLOSED;
    private TabState keyTabState = TabState.CLOSED;
    private String language = "en_US";
    private BellType bellType = BellType.Visual;

    public ApplicationSettings() {
        PasswordStorage newPasswordManager;
        try {
            newPasswordManager = new PasswordStorage();
        } catch(final CryptoException e) {
            newPasswordManager = null;
            LOGGER.error("Could not initialize password manager backend; " +
            		"ignoring it completely", e);
        }
        this.passwordStorage = newPasswordManager;
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

    public PasswordStorage getPasswordStorage() {
        return passwordStorage;
    }

    public boolean getUnlockKeysOnStartup() {
        return unlockKeysOnStartup;
    }

    public void setUnlockKeysOnStartup(final boolean unlockKeysOnStartup) {
        this.unlockKeysOnStartup = unlockKeysOnStartup;
    }

    public BellType getBellType() {
        return bellType;
    }

    public void setBellType(final BellType bellType) {
        this.bellType = bellType;
    }
}
