package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Map.Entry;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.ProfileEvent;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.tabpanel.SplitDirection;

public class MainWindowToolbar implements ProfileEvent {
    private final MainWindow parent;
    private final JComboBox comboBox = createComboBox();
    private final JToolBar toolBar = new JToolBar("ssh");
    private final Application application;

    public MainWindowToolbar(final Application application, final MainWindow parent) {
        this.parent = parent;
        this.application = application;
        application.profileEvents.register(this);

        toolBar.setFloatable(false);
        toolBar.add(comboBox);
        toolBar.add(createConnectButton());
        toolBar.add(createHSplitButton());
        toolBar.add(createVSplitButton());
        profilesUpdated();
    }

    private JButton createConnectButton() {
        final JButton button = createButton(t("mainwindow.toolbar.connect", "connect"), "connect_established");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                connectToSelectedProfile();
            }
        });
        return button;
    }

    private JButton createHSplitButton() {
        final JButton button = createButton(t("mainwindow.toolbar.hsplit", "horizontal split"),
                "view_left_right");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.split(SplitDirection.HORIZONTAL);
            }
        });
        return button;
    }

    private JButton createVSplitButton() {
        final JButton button = createButton(t("mainwindow.toolbar.vsplit", "vertical split"),
                "view_top_bottom");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.split(SplitDirection.VERTICAL);
            }
        });
        return button;
    }

    private JComboBox createComboBox() {
        final JComboBox comboBox = new JComboBox();
        comboBox.setPreferredSize(new Dimension(200, comboBox.getPreferredSize().height));
        comboBox.setMaximumSize(comboBox.getPreferredSize());
        return comboBox;
    }

    private JButton createButton(final String text, final String image) {
        final URL icon = getClass().getResource("/gfx/" + image + ".png");
        final JButton button = new JButton();
        button.setIcon(new ImageIcon(icon, text));
        button.setToolTipText(text);
        return button;
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    private void connectToSelectedProfile() {
        final String profileName = (String) comboBox.getSelectedItem();
        if (profileName != null) {
            final AWTProfile profile = application.settings.getProfiles().get(profileName);
            if (profile != null) {
                parent.connect(profile);
            }
        }
    }

    @Override
    public void profilesUpdated() {
        final String[] profileNames = new String[application.settings.getProfiles().size()];
        final Object oldValue = comboBox.getSelectedItem();
        int i = 0;

        for (final Entry<String, AWTProfile> p : application.settings.getProfiles().entrySet()) {
            profileNames[i++] = p.getKey();
        }

        final ComboBoxModel model = new DefaultComboBoxModel(profileNames);
        comboBox.setSelectedItem(oldValue);
        comboBox.setModel(model);
    }
}
