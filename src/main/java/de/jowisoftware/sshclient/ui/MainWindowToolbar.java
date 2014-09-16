package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.ProfileEvent;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.about.AboutDialog;
import de.jowisoftware.sshclient.ui.tabpanel.SplitDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class MainWindowToolbar implements ProfileEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindowToolbar.class);

    private final MainWindow parent;
    private final JComboBox<String> comboBox = createComboBox();
    private final JToolBar toolBar = new JToolBar("ssh");
    private final Application application;

    public MainWindowToolbar(final Application application, final MainWindow parent) {
        this.parent = parent;
        this.application = application;
        application.profileEvents.register(this);

        toolBar.setFloatable(false);
        toolBar.add(comboBox);
        toolBar.add(createConnectButton());
        toolBar.add(createDirectConnectButton());
        toolBar.addSeparator();
        toolBar.add(createHSplitButton());
        toolBar.add(createVSplitButton());
        toolBar.add(createSeparator());
        toolBar.add(createWebpage());
        toolBar.add(createAbout());

        profilesUpdated();
    }

    private JSeparator createSeparator() {
        return new JSeparator(SwingConstants.VERTICAL);
    }

    private JButton createDirectConnectButton() {
        final JButton button = createButton(
                t("mainwindow.toolbar.directconnect", "direct connect"),
                "connect_creating");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.connectToCustomProfile();
            }
        });
        return button;
    }

    private JButton createConnectButton() {
        final JButton button = createButton(t("mainwindow.toolbar.connect",
                "connect"), "connect_established");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                connectToSelectedProfile();
            }
        });
        return button;
    }

    private JButton createHSplitButton() {
        final JButton button = createButton(t("mainwindow.toolbar.hsplit",
                "horizontal split"),
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
        final JButton button = createButton(t("mainwindow.toolbar.vsplit",
                "vertical split"),
                "view_top_bottom");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.split(SplitDirection.VERTICAL);
            }
        });
        return button;
    }

    private JButton createWebpage() {
        final JButton button = createButton(t("mainwindow.toolbar.webpage",
                "Open project's webpage"), "trac");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final URI uri;
                try {
                    uri = new URI("http://jowisoftware.de/trac/ssh");
                } catch (final URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }

                boolean error = false;

                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(
                                uri);
                    } catch (final Exception ex) {
                        LOGGER.warn("Could not open webpage", ex);
                        error = true;
                    }
                } else {
                    error = true;
                }

                if (error) {
                    JOptionPane.showMessageDialog(parent,
                            t("error.webPage", "Could not open webpage: %s",
                                    uri.toString()), t("error", "Error"),
                            JOptionPane.ERROR_MESSAGE, null);
                }
            }
        });
        return button;
    }

    private JButton createAbout() {
        final JButton button = createButton(t("mainwindow.toolbar.about",
                "About"), "info");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new AboutDialog(parent).setVisible(true);
            }
        });
        return button;
    }

    private JComboBox<String> createComboBox() {
        final JComboBox<String> comboBox = new JComboBox<>();
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
        Arrays.sort(profileNames, new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });

        final ComboBoxModel<String> model = new DefaultComboBoxModel<>(
                profileNames);
        comboBox.setSelectedItem(oldValue);
        comboBox.setModel(model);
    }
}
