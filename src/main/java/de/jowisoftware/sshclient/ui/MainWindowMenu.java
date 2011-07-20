package de.jowisoftware.sshclient.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(createConnectEntry());
        fileMenu.add(createQuitMenuEntry());

        return fileMenu;
    }


    private JMenu createViewMenu() {
        final JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('v');

        viewMenu.add(createLogVisibilityEntry());
        viewMenu.add(createKeyAgentVisibilityEntry());

        return viewMenu;
    }


    private JMenuItem createLogVisibilityEntry() {
        final JMenuItem entry = new JMenuItem("Show Logs");
        entry.setMnemonic('l');

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.setLogTabVisibility(true);
            }
        });

        return entry;
    }

    private JMenuItem createKeyAgentVisibilityEntry() {
        final JMenuItem entry = new JMenuItem("Key Agent");
        entry.setMnemonic('a');

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.setKeyTabVisibility(true);
            }
        });

        return entry;
    }

    private JMenuItem createConnectEntry() {
        final JMenuItem entry = new JMenuItem("Connect...");
        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.connectToCustomProfile();
            }
        });
        return entry;
    }

    private JMenuItem createQuitMenuEntry() {
        final JMenuItem entry = new JMenuItem("Quit");
        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.dispose();
            }
        });
        return entry;
    }
}
