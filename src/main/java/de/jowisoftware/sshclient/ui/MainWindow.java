package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.ApplicationSettings.TabState;
import de.jowisoftware.sshclient.application.persisting.XMLPersister;
import de.jowisoftware.sshclient.debug.PerformanceLogger;
import de.jowisoftware.sshclient.log.LogPanel;
import de.jowisoftware.sshclient.ui.settings.ConnectDialog;
import de.jowisoftware.sshclient.ui.tabpanel.RedrawingTabPane;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;
import de.jowisoftware.sshclient.util.ApplicationUtils;
import de.jowisoftware.sshclient.util.FontUtils;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = -2951599770927217249L;
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class);

    private final Application application;

    private final MainWindowMenu menu;
    private final MainWindowToolbar toolBar;

    private final RedrawingTabPane pane = createTabbedPane();
    private final JComponent logPanel = new LogPanel();
    private final PrivateKeyTab keyPanel;

    public MainWindow(final Application application) {
        super("SSH");
        this.application = application;
        application.setMainWindow(this);

        FontUtils.fillAsyncCache();
        ApplicationUtils.saveStartupMethod();

        menu = new MainWindowMenu(application, this);
        toolBar = new MainWindowToolbar(application, this);
        keyPanel = new PrivateKeyTab(application, this);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initWindowElements();
    }

    @Override
    public void dispose() {
        // TODO: if state is connecting, we get a problem here

        persistTabStates();

        pane.stopRedraw();
        while(pane.getTabCount() > 0) {
            final Component component = pane.getComponentAt(0);
            if (component instanceof ConnectionFrame) {
                final ConnectionFrame tab = (ConnectionFrame) component;
                tab.close();
            }
            pane.removeTabAt(0);
        }

        try {
            new XMLPersister(application.settings).save(
                    new File(application.sshDir, "settings.xml"));
        } catch(final RuntimeException e) {
            LOGGER.error("Could not save settings", e);
        }
        super.dispose();
        PerformanceLogger.INSTANCE.quit();
    }

    private void persistTabStates() {
        if (pane.indexOfComponent(keyPanel) >= 0
                && application.settings.getKeyTabState() == TabState.CLOSED) {
            application.settings.setKeyTabState(TabState.OPENED);
        } else if (pane.indexOfComponent(keyPanel) == -1
                && application.settings.getKeyTabState() == TabState.OPENED) {
            application.settings.setKeyTabState(TabState.CLOSED);
        }

        if (pane.indexOfComponent(logPanel) >= 0
                && application.settings.getLogTabState() == TabState.CLOSED) {
            application.settings.setLogTabState(TabState.OPENED);
        } else if (pane.indexOfComponent(logPanel) == -1
                && application.settings.getLogTabState() == TabState.OPENED) {
            application.settings.setLogTabState(TabState.CLOSED);
        }
    }

    private void initWindowElements() {
        setJMenuBar(menu.getMenuBar());

        setLayout(new BorderLayout());

        add(pane, BorderLayout.CENTER);
        add(toolBar.getToolBar(), BorderLayout.NORTH);

        initTabs();

        setSize(640, 480);
        setLocationRelativeTo(null);
    }

    private RedrawingTabPane createTabbedPane() {
        final RedrawingTabPane tabbedPane = new RedrawingTabPane();
        tabbedPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                final Component selectedComponent = tabbedPane.getSelectedComponent();
                if (selectedComponent instanceof ConnectionFrame) {
                    ((ConnectionFrame) selectedComponent).takeFocusWithKey(e);
                }
            }
        });

        tabbedPane.addChangeListener(createTabPaneMenuListener());

        return tabbedPane;
    }

    private ChangeListener createTabPaneMenuListener() {
        return new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                final Component selectedComponent = pane.getSelectedComponent();
                SessionMenu sessionMenu = null;
                if (selectedComponent instanceof ConnectionFrame) {
                    sessionMenu = ((ConnectionFrame) selectedComponent).getSessionMenu();
                }

                if (sessionMenu != null) {
                    menu.setSessionMenu(sessionMenu);
                } else {
                    menu.unsetSessionMenu();
                }
            }
        };
    }

    private void initTabs() {
        if (application.settings.getKeyTabState() == TabState.ALYWAYS_OPEN
                || application.settings.getKeyTabState() == TabState.OPENED) {
            setKeyTabVisibility(true);
        }

        if (application.settings.getLogTabState() == TabState.ALYWAYS_OPEN
                || application.settings.getLogTabState() == TabState.OPENED) {
            setLogTabVisibility(true);
        }
    }

    public void setKeyTabVisibility(final boolean isVisible) {
        setPanelVisibility(isVisible, keyPanel, t("mainwindow.tabs.keys", "keys"));
    }

    public void setLogTabVisibility(final boolean isVisible) {
        setPanelVisibility(isVisible, logPanel, t("mainwindow.tabs.logs", "logs"));
    }

    private void setPanelVisibility(final boolean isVisible,
            final JComponent panel, final String title) {
        final int tabPos = pane.indexOfComponent(panel);

        if (isVisible) {
            if (tabPos == -1) {
                pane.addTab(title, panel);
                pane.setTabComponentAt(pane.getTabCount() - 1,
                        new ClosableTabComponent(title, pane).create());
            }
            pane.setSelectedComponent(panel);
        } else if (isVisible && tabPos >= 0) {
            pane.remove(panel);
        }
    }

    public void connect(final AWTProfile profile) {
        final AWTProfile safeProfile = new AWTProfile(profile);
        final ConnectionFrame sshFrame = new ConnectionFrame(application, safeProfile, pane);
        pane.addTab(safeProfile.getDefaultTitle(), sshFrame);
        pane.setTabComponentAt(pane.getTabCount() - 1,
                sshFrame.createTabComponent());
        pane.setSelectedComponent(sshFrame);
        sshFrame.connect();
        sshFrame.takeFocus();
    }

    public void connectToCustomProfile() {
        final ConnectDialog connectDialog = new ConnectDialog(this);
        final AWTProfile profile = connectDialog.createProfile();
        connectDialog.dispose();
        if (profile != null) {
            connect(profile);
        }
    }
}
