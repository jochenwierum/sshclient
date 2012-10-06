package de.jowisoftware.sshclient.ui.about;

import de.jowisoftware.sshclient.util.ApplicationUtils.VersionInformation;

public interface UpdateCheckResult {
    void reportResult(VersionInformation version);
}
