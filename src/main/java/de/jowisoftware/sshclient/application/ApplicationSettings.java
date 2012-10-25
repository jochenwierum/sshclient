package de.jowisoftware.sshclient.application;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.jowisoftware.sshclient.application.AWTApplicationSettings.TabState;
import de.jowisoftware.sshclient.encryption.PasswordStorage;
import de.jowisoftware.sshclient.terminal.Profile;

public interface ApplicationSettings<T extends Profile<?>> {
    Map<String, T> getProfiles();

    List<File> getKeyFiles();

    TabState getLogTabState();
    void setLogTabState(final TabState state);

    void setKeyTabState(final TabState state);
    TabState getKeyTabState();

    String getLanguage();
    void setLanguage(final String language);

    PasswordStorage getPasswordStorage();

    BellType getBellType();
    void setBellType(final BellType bellType);

    T newDefaultProfile();
    void setDefaultProfile(final T defaultProfile);
}