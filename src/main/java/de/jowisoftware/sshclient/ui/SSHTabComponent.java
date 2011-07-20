package de.jowisoftware.sshclient.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import de.jowisoftware.sshclient.settings.Profile;

public class SSHTabComponent extends ClosableTabComponent implements MouseListener {
    private static final long serialVersionUID = 3033441642594395407L;
    protected final Profile connectionInfo;

    public SSHTabComponent(final SSHFrame parent, final Profile info,
            final JTabbedPane pane) {
        super(null, parent, pane);

        this.connectionInfo = info;

        init();
    }

    @Override
    protected JLabel createLabel() {
        final JLabel label = new JLabel(connectionInfo.getTitle()) {
            private static final long serialVersionUID = 1007042595244781174L;

            @Override
            public String getText() {
                return connectionInfo.getTitle();
            }
        };
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
        return label;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        ((SSHFrame) parent).close();
        super.mouseClicked(e);
    }
}
