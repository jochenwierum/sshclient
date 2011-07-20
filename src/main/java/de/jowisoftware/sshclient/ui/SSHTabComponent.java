package de.jowisoftware.sshclient.ui;

import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import de.jowisoftware.sshclient.settings.Profile;

public class SSHTabComponent extends AbstractClosableTabcomponent {
    private static final long serialVersionUID = 3033441642594395407L;
    protected final Profile profile;

    public SSHTabComponent(final SSHFrame parent, final Profile profile,
            final JTabbedPane pane) {
        super(parent, pane);
        this.profile = profile;
        init();
    }

    @Override
    protected JLabel createLabel() {
        return new JLabel("") {
            private static final long serialVersionUID = 1007042595244781174L;

            @Override
            public String getText() {
                return profile.getTitle();
            }
        };
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        ((SSHFrame) parent).close();
        super.mouseClicked(e);
    }
}
