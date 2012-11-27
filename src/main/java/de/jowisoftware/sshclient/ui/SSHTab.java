package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.tabpanel.Tab;
import de.jowisoftware.sshclient.ui.tabpanel.closable.ClosableTabListener;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawableTab;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;

public class SSHTab implements RedrawableTab {
    private final SSHTabTitleComponent title;
    private final ConnectionPanel content;

    public SSHTab(final AWTProfile profile, final Application application, final RedrawingTabPanel parent) {
        this.title = new SSHTabTitleComponent(profile, this);
        this.content = new ConnectionPanel(application, profile, parent, this);

        title.addListener(new ClosableTabListener() {
            @Override
            public void closeTab(final Tab tab) {
                content.close();
                parent.closeTab(SSHTab.this);
            }
        });
    }

    @Override
    public ConnectionPanel getContent() {
        return content;
    }

    @Override
    public SSHTabTitleComponent getTitleContent() {
        return title;
    }

    @Override
    public void freeze() {
        content.freeze();

    }

    @Override
    public void unfreeze() {
        content.unfreeze();
    }

    @Override
    public void redraw() {
        content.redraw();
    }
}
