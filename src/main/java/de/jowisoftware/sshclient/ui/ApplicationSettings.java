package de.jowisoftware.sshclient.ui;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.jowisoftware.sshclient.ConnectionInfo;

public class ApplicationSettings {
    public enum FrameCloseBehaviour {
        ALWAYS, NEVER, WITHOUT_ERRORS
    }

    public enum TabState {
        OPENED_ON_START(false), CLOSED_ON_START(false),
            SAVE_AND_CLOSED(true), SAVE_AND_OPENED(true);

        private final boolean saveState;
        private TabState(final boolean saveState) {
            this.saveState = saveState;
        }

        public boolean saveTabState() {
            return saveState;
        }
    }

    private HashMap<String, ConnectionInfo> connections;
    private List<File> keyFiles;

    private boolean unlockKeysOnStart;
    private TabState logTabState;
    private TabState keyTabState;
}
