package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.tabpanel.Tab;
import de.jowisoftware.sshclient.ui.tabpanel.closable.ClosableTabListener;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;

public abstract class AbstractSSHTab<T extends AbstractSSHConnectionPanel<?>> implements Tab {
    protected final SSHTabTitleComponent title;
    protected final T content;

    protected AbstractSSHTab(final AWTProfile profile, final Application application, final RedrawingTabPanel parent) {
        this.title = new SSHTabTitleComponent(profile, this);
        this.content = makeContentPanel(profile, application, parent);

        title.addListener(new ClosableTabListener() {
            @Override
            public void closeTab(final Tab tab) {
                content.close();
                parent.closeTab(AbstractSSHTab.this);
            }
        });
    }

    protected abstract T makeContentPanel(AWTProfile profile, Application application, RedrawingTabPanel parent);

    @Override
    public T getContent() {
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
}
