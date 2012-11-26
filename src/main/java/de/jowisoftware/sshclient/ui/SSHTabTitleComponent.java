package de.jowisoftware.sshclient.ui;

import javax.swing.JLabel;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.tabpanel.Tab;
import de.jowisoftware.sshclient.ui.tabpanel.closable.ClosableTabTitleComponent;

public class SSHTabTitleComponent extends ClosableTabTitleComponent {
    private static final long serialVersionUID = 3033441642594395407L;

    public SSHTabTitleComponent(final AWTProfile profile, final Tab tab) {
        super(tab, new JLabel(profile.getDefaultTitle()));
    }

    public void updateLabel(final String title) {
        label.setText(title);
    }

    public String getLabel() {
        return label.getText();
    }
}
