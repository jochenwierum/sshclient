package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import de.jowisoftware.sshclient.ui.settings.ProfilesDialog;
import de.jowisoftware.sshclient.util.JarUtils;


public class MainWindowMenu {
    private final JMenu dummySessionMenu = createEmptySessionMenu();
    private final JMenuBar menu = createMenuBar();

    private final MainWindow parent;
    private JMenu sessionMenu;

    public MainWindowMenu(final MainWindow parent) {
        this.parent = parent;
        sessionMenu = dummySessionMenu;
    }

    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(dummySessionMenu);
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    private JMenu createHelpMenu() {
        final JMenu helpMenu = new JMenu(t("mainwindow.menu.help", "Help"));
        helpMenu.setMnemonic(m("mainwindow.menu.help", 'h'));

        helpMenu.add(createAboutEntry());

        return helpMenu;
    }

    private JMenu createEmptySessionMenu() {
        final JMenu dummyMenu = new JMenu(t("mainwindow.menu.session", "Session"));
        dummyMenu.setEnabled(false);
        return dummyMenu;
    }

    public JMenuBar getMenuBar() {
        return menu;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu(t("mainwindow.menu.file", "File"));
        fileMenu.setMnemonic(m("mainwindow.menu.file", 'f'));

        fileMenu.add(createConnectEntry());
        fileMenu.add(createSessionsEntry());
        fileMenu.add(createMasterPasswordEntry());
        fileMenu.add(createQuitMenuEntry());

        return fileMenu;
    }

    private JMenu createViewMenu() {
        final JMenu viewMenu = new JMenu(t("mainwindow.menu.view", "View"));
        viewMenu.setMnemonic(m("mainwindow.menu.view", 'v'));

        viewMenu.add(createLogVisibilityEntry());
        viewMenu.add(createKeyAgentVisibilityEntry());

        return viewMenu;
    }


    private JMenuItem createLogVisibilityEntry() {
        final JMenuItem entry = new JMenuItem(t("mainwindow.menu.show_logs", "Show Logs"));
        entry.setMnemonic(m("mainwindow.menu.show_logs", 'l'));

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.setLogTabVisibility(true);
            }
        });

        return entry;
    }

    private JMenuItem createKeyAgentVisibilityEntry() {
        final JMenuItem entry = new JMenuItem(t("mainwindow.menu.key_agent", "Key Agent"));
        entry.setMnemonic(m("mainwindow.menu.key_agent", 'a'));

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.setKeyTabVisibility(true);
            }
        });

        return entry;
    }

    private JMenuItem createSessionsEntry() {
        final JMenuItem entry = new JMenuItem(t("mainwindow.menu.profiles", "Profiles"));
        entry.setMnemonic(m("mainwindow.menu.profiles", 'p'));

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ProfilesDialog dialog = new ProfilesDialog(parent,
                        parent.settings);
                dialog.showSettings();
                dialog.dispose();
                parent.updateProfiles();
            }
        });
        return entry;
    }

    private JMenuItem createConnectEntry() {
        final JMenuItem entry = new JMenuItem(t("mainwindow.menu.connect", "Connect"));
        entry.setMnemonic(m("mainwindow.menu.connect", 'c'));
        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.connectToCustomProfile();
            }
        });
        return entry;
    }

    private JMenuItem createMasterPasswordEntry() {
        final JMenuItem entry = new JMenuItem(t("mainwindow.menu.masterpassword", "Change master password"));
        entry.setMnemonic(m("mainwindow.menu.masterpassword", 'm'));
        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.passwordManager.changeMasterPassword();
            }
        });
        return entry;
    }

    private JMenuItem createQuitMenuEntry() {
        final JMenuItem entry = new JMenuItem(t("mainwindow.menu.quit", "Quit"));
        entry.setMnemonic(m("mainwindow.menu.quit", 'q'));
        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.dispose();
            }
        });
        return entry;
    }

    public void setSessionMenu(final SessionMenu sessionMenu) {
        updateSessionMenu(sessionMenu);
    }

    public void unsetSessionMenu() {
        updateSessionMenu(dummySessionMenu);
    }

    private void updateSessionMenu(final JMenu newMenu) {
        final int index = menu.getComponentIndex(sessionMenu);
        menu.remove(sessionMenu);
        this.sessionMenu = newMenu;
        menu.add(sessionMenu, index);

        menu.invalidate();
        menu.repaint();
    }


    private JMenuItem createAboutEntry() {
        final JMenuItem entry = new JMenuItem(t("mainwindow.menu.about", "about"));
        entry.setMnemonic(m("mainwindow.menu.about", 'a'));

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                JOptionPane.showMessageDialog(parent, "Version: " + JarUtils.getVersion(),
                        "SSH", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return entry;
    }

}
