package de.jowisoftware.sshclient.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ApplicationSettings {
    public enum TabState {
        OPENED, CLOSED, ALYWAYS_OPEN, ALWAYS_CLOSED
    }

    private final Map<String, Profile> profiles = new HashMap<String, Profile>();
    private final List<File> keyFiles = new ArrayList<File>();

    private TabState logTabState = TabState.CLOSED;
    private TabState keyTabState = TabState.CLOSED;
    private String language = "en_US";

    /*
    public ApplicationSettings() {
        Profile profile = new Profile();
        profile.setHost("jowisoftware.de");
        profile.setUser("jochen");
        profiles.put("jowisoftware", profile);

        profile = new Profile();
        profile.setHost("home.inf.h-brs.de");
        profile.setUser("jwieru2s");
        profiles.put("fh", profile);
    }
    */

    public Map<String, Profile> getProfiles() {
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
}
