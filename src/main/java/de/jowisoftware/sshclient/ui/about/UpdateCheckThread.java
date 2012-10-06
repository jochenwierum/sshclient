package de.jowisoftware.sshclient.ui.about;

import de.jowisoftware.sshclient.util.ApplicationUtils;
import de.jowisoftware.sshclient.util.ApplicationUtils.VersionInformation;

final class UpdateCheckThread extends Thread {
    private final UpdateCheckResult result;

    public UpdateCheckThread(final UpdateCheckResult result) {
        this.result = result;
        setDaemon(true);
    }

    @Override
    public void run() {
        final VersionInformation version = ApplicationUtils.getAvailableUpdateVersion();
        result.reportResult(version);
    }
}