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

public class AWTApplicationSettings implements ApplicationSettings<AWTProfile> {
    public enum TabState {
        OPENED, CLOSED, ALYWAYS_OPEN, ALWAYS_CLOSED
    }

    private static final Logger LOGGER = Logger
            .getLogger(AWTApplicationSettings.class);

    private AWTProfile defaultProfile = new AWTProfile();
    private final Map<String, AWTProfile> profiles = new HashMap<String, AWTProfile>();
    private final List<File> keyFiles = new ArrayList<File>();

    private final PasswordStorage passwordStorage;
    private final boolean unlockKeysOnStartup = false;

    private TabState logTabState = TabState.CLOSED;
    private TabState keyTabState = TabState.CLOSED;
    private String language = "en_US";
    private BellType bellType = BellType.Visual;

    public AWTApplicationSettings() {
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

    @Override
    public Map<String, AWTProfile> getProfiles() {
        return profiles;
    }

    @Override
    public List<File> getKeyFiles() {
        return keyFiles;
    }

    @Override
    public TabState getLogTabState() {
        return logTabState;
    }

    @Override
    public TabState getKeyTabState() {
        return keyTabState;
    }

    @Override
    public void setKeyTabState(final TabState state) {
        keyTabState = state;
    }

    @Override
    public void setLogTabState(final TabState state) {
        logTabState = state;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(final String language) {
        this.language = language;
    }

    @Override
    public PasswordStorage getPasswordStorage() {
        return passwordStorage;
    }

    @Override
    public BellType getBellType() {
        return bellType;
    }

    @Override
    public void setBellType(final BellType bellType) {
        this.bellType = bellType;
    }

    @Override
    public AWTProfile newDefaultProfile() {
        return new AWTProfile(defaultProfile);
    }

    @Override
    public void setDefaultProfile(final AWTProfile defaultProfile) {
        this.defaultProfile = defaultProfile;
    }
}
