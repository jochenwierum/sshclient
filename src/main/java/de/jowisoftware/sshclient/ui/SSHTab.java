package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawableTab;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;

public class SSHTab extends AbstractSSHTab<ConnectionPanel> implements RedrawableTab {

    public SSHTab(final AWTProfile profile, final Application application, final RedrawingTabPanel parent) {
        super(profile, application, parent);
    }

    @Override
    protected ConnectionPanel makeContentPanel(final AWTProfile profile, final Application application, final RedrawingTabPanel parent) {
        return new ConnectionPanel(application, profile, parent, this);
    }

    @Override
    public void redraw() {
        content.redraw();
    }
}
