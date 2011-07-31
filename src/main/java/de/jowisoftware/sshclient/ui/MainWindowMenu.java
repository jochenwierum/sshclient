package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.jowisoftware.sshclient.MainWindow;

public class MainWindowMenu {
    private final MainWindow parent;
    private final JMenuBar menu;

    public MainWindowMenu(final MainWindow parent) {
        this.parent = parent;

        menu = new JMenuBar();
        final JMenu fileMenu = createFileMenu();
        final JMenu viewMenu = createViewMenu();
        menu.add(fileMenu);
        menu.add(viewMenu);
    }

    public JMenuBar getMenuBar() {
        return menu;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu(t("mainwindow.menu.file", "File"));
        fileMenu.setMnemonic(m("mainwindow.menu.file", 'f'));

        fileMenu.add(createConnectEntry());
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

    private JMenuItem createConnectEntry() {
        final JMenuItem entry = new JMenuItem(t("mainwindow.menu.connect", "Connect..."));
        entry.setMnemonic(m("mainwindow.menu.connect", 'c'));
        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.connectToCustomProfile();
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
}
