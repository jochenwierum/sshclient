package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;

public class SftpTab extends AbstractSSHTab {
    public SftpTab(final AWTProfile profile, final Application application, final RedrawingTabPanel parent) {
        super(profile, application, parent);
    }

    @Override
    protected ConnectionPanel makeContentPanel(final AWTProfile profile, final Application application, final RedrawingTabPanel parent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
