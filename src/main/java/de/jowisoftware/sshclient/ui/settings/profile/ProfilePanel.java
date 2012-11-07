package de.jowisoftware.sshclient.ui.settings.profile;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.settings.AbstractOptionPanel;

public class ProfilePanel extends JPanel {
    private static final long serialVersionUID = 663223636542133238L;

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final List<AbstractOptionPanel> saveables = new ArrayList<AbstractOptionPanel>();
    private final MainPanel mainPanel;

    public ProfilePanel(final AWTProfile profile, final String profileName,
            final boolean profileNameIsSettable, final Window parent) {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        mainPanel = new MainPanel(profile, profileName, profileNameIsSettable, parent);
        addTabPane(mainPanel);
        addTabPane(new GraphicsPanel(profile.getGfxSettings(), parent));
        addTabPane(new ForwardingPane(profile, parent));
        addTabPane(new AdvancedPanel(profile, parent));
    }

    private void addTabPane(final AbstractOptionPanel panel) {
        panel.setOpaque(false);
        final JScrollPane scrollPane = new JScrollPane(panel);

        scrollPane.setOpaque(false);
        tabbedPane.addTab(panel.getTitle(), scrollPane);

        if (panel instanceof AbstractOptionPanel) {
            saveables.add(panel);
        }
    }

    public void applyUnboundValues() {
        for (final AbstractOptionPanel saveable : saveables) {
            saveable.save();
        }
    }

    public String getProfileName() {
        return mainPanel.getProfileName();
    }
}
