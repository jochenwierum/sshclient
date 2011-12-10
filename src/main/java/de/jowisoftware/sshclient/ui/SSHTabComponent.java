package de.jowisoftware.sshclient.ui;

import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import de.jowisoftware.sshclient.ui.terminal.AWTProfile;

public class SSHTabComponent extends AbstractClosableTabcomponent {
    private static final long serialVersionUID = 3033441642594395407L;
    private final ConnectionFrame parent;
    private final JLabel label;

    public SSHTabComponent(final ConnectionFrame parent, final AWTProfile profile,
            final JTabbedPane pane) {
        super(pane);
        this.parent = parent;

        label = new JLabel(profile.getDefaultTitle());
    }

    @Override
    protected JLabel createLabel() {
        return label;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        (parent).close();
        super.mouseClicked(e);
    }

    public void updateLabel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label.invalidate();
            }
        });
    }

    public void updateLabel(final String title) {
        label.setText(title);
    }
}
