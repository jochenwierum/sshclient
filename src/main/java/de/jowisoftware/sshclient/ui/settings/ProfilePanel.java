package de.jowisoftware.sshclient.ui.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;

public class ProfilePanel extends JPanel {
    private static final long serialVersionUID = 663223636542133238L;

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final List<AbstractOptionPanel> saveables = new ArrayList<AbstractOptionPanel>();
    private final MainPanel mainPanel;

    public ProfilePanel(final AWTProfile profile, final String profileName,
            final boolean profileNameIsSettable) {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        mainPanel = new MainPanel(profile, profileName, profileNameIsSettable);
        addTabPane(mainPanel);
        addTabPane(new GraphicsPanel(profile.getGfxSettings()));
        addTabPane(new ForwardingPane(profile));
        addTabPane(new AdvancedPanel(profile));
    }

    private void addTabPane(final AbstractOptionPanel panel) {
        final JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.add(panel);

        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width,
                panel.getMinimumSize().height));

        final JScrollPane scrollPane = new JScrollPane(wrapper);
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
