package de.jowisoftware.sshclient.application.settings.awt;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.BellType;
import de.jowisoftware.sshclient.application.settings.TabState;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.Persist;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.TraversalType;
import de.jowisoftware.sshclient.encryption.CryptoException;
import de.jowisoftware.sshclient.encryption.PasswordStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AWTApplicationSettings implements ApplicationSettings<AWTProfile> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWTApplicationSettings.class);

    @Persist(traversalType = TraversalType.RECURSIVE)
    private AWTProfile defaultProfile = new AWTProfile();

    @Persist(value = "profiles", traversalType = TraversalType.MAP,
            traverseListAndMapChildrenRecursively = true, targetClass = AWTProfile.class, targetClass2 = String.class)
    private final Map<String, AWTProfile> profiles = new HashMap<>();

    @Persist(value = "keys", traversalType = TraversalType.LIST, targetClass = File.class)
    private final List<File> keyFiles = new ArrayList<>();

    @Persist(value = "passwords", traversalType = TraversalType.RECURSIVE)
    private final PasswordStorage passwordStorage;

    @Persist("logtab/@state") private TabState logTabState = TabState.CLOSED;
    @Persist("keytab/@state") private TabState keyTabState = TabState.CLOSED;

    @Persist private String language = "en_US";
    @Persist private BellType bellType = BellType.VISUAL;

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
