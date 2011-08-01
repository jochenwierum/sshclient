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

import de.jowisoftware.sshclient.settings.Profile;

public class MainWindowToolbar {
    private final MainWindow parent;
    private JComboBox comboBox;
    private final JToolBar toolBar;

    public MainWindowToolbar(final MainWindow parent) {
        this.parent = parent;

        toolBar = new JToolBar("ssh");
        toolBar.setFloatable(false);

        createComboBox();
        toolBar.add(comboBox);

        final JButton button = createConnectButton();
        toolBar.add(button);
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

    private void createComboBox() {
        comboBox = new JComboBox();
        comboBox.setPreferredSize(new Dimension(200, comboBox.getPreferredSize().height));
        comboBox.setMaximumSize(comboBox.getPreferredSize());
        updateProfiles();
    }

    public void updateProfiles() {
        final String[] profileNames = new String[parent.settings.getProfiles().size()];
        final Object oldValue = comboBox.getSelectedItem();
        int i = 0;

        for (final Entry<String, Profile> p : parent.settings.getProfiles().entrySet()) {
            profileNames[i++] = p.getKey();
        }

        final ComboBoxModel model = new DefaultComboBoxModel(profileNames);
        comboBox.setSelectedItem(oldValue);
        comboBox.setModel(model);
    }

    private JButton createButton(final String text, final String image) {
        final URL icon = getClass().getResource("/gfx/" + image + ".png");
        final JButton button = new JButton();
        button.setIcon(new ImageIcon(icon, text));
        return button;
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    private void connectToSelectedProfile() {
        final String profileName = (String) comboBox.getSelectedItem();
        if (profileName != null) {
            final Profile profile = parent.settings.getProfiles().get(profileName);
            if (profile != null) {
                parent.connect(profile);
            }
        }
    }
}
