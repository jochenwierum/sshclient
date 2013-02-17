package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.jowisoftware.sshclient.application.settings.persistence.Persister;
import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.arguments.ArgumentParser;
import de.jowisoftware.sshclient.application.arguments.ArgumentParserCallback;
import de.jowisoftware.sshclient.application.settings.TabState;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.debug.PerformanceLogger;
import de.jowisoftware.sshclient.log.LogTab;
import de.jowisoftware.sshclient.ui.settings.profile.ConnectDialog;
import de.jowisoftware.sshclient.ui.tabpanel.SplitDirection;
import de.jowisoftware.sshclient.ui.tabpanel.Tab;
import de.jowisoftware.sshclient.ui.tabpanel.TabPanelListener;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;
import de.jowisoftware.sshclient.util.ApplicationUtils;
import de.jowisoftware.sshclient.util.FontUtils;
import de.jowisoftware.sshclient.util.SwingUtils;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = -2951599770927217249L;

    private final Application application;

    private final MainWindowMenu menu;
    private final MainWindowToolbar toolBar;

    private final RedrawingTabPanel tabPanel = createTabPane();
    private final LogTab logTab = new LogTab(tabPanel);
    private final PrivateKeyTab keyTab;

    public MainWindow(final Application application) {
        super("SSH");
        this.application = application;
        application.setMainWindow(this);

        FontUtils.fillAsyncCache();
        ApplicationUtils.saveStartupMethod();

        menu = new MainWindowMenu(application, this);
        toolBar = new MainWindowToolbar(application, this);
        keyTab = new PrivateKeyTab(application, this, tabPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initWindowElements();
    }

    @Override
    public void dispose() {
        // This code assumes that connecting happens in the Swing EDT
        // otherwise, calling close() in middle of establishing a
        // connection could be a problem

        persistTabStates();

        tabPanel.stopRedraw();
        for (final Tab tab : tabPanel.getTabs()) {
            if (tab instanceof SSHTab) {
                ((SSHTab) tab).getContent().close();
            }
            tabPanel.closeTab(tab);
        }

        new Persister(new File(application.sshDir, "settings.xml"))
                .persist(application.settings);
        super.dispose();
        PerformanceLogger.INSTANCE.quit();
    }

    private void persistTabStates() {
        final boolean logTabOpen = tabPanel.containsTab(logTab);
        final boolean keyTabOpen = tabPanel.containsTab(keyTab);
        final TabState initialLogState = application.settings.getLogTabState();
        final TabState initialKeyState = application.settings.getKeyTabState();

        if (logTabOpen && initialLogState == TabState.CLOSED) {
            application.settings.setLogTabState(TabState.OPEN);
        } else if (!logTabOpen && initialLogState == TabState.OPEN) {
            application.settings.setLogTabState(TabState.CLOSED);
        }

        if (keyTabOpen && initialKeyState == TabState.CLOSED) {
            application.settings.setKeyTabState(TabState.OPEN);
        } else if (!keyTabOpen && initialKeyState == TabState.OPEN) {
            application.settings.setKeyTabState(TabState.CLOSED);
        }
    }

    private void initWindowElements() {
        setJMenuBar(menu.getMenuBar());

        setLayout(new BorderLayout());

        add(tabPanel.getComponent(), BorderLayout.CENTER);
        add(toolBar.getToolBar(), BorderLayout.NORTH);

        initTabs();

        setSize(640, 480);
        setLocationRelativeTo(null);
    }

    private RedrawingTabPanel createTabPane() {
        final RedrawingTabPanel tabbedPane = new RedrawingTabPanel();
        tabbedPane.addListener(createTabPaneMenuListener());

        tabbedPane.setKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                final Tab tab = tabbedPane.getActiveTab();
                if (tab instanceof SSHTab) {
                    ((SSHTab) tab).getContent().takeFocusWithKey(e);
                }
            }
        });

        return tabbedPane;
    }

    private TabPanelListener createTabPaneMenuListener() {
        return new TabPanelListener() {
            @Override
            public void selectionChanged(final Tab newSelection) {
                SessionMenu sessionMenu = null;
                if (newSelection != null) {
                    final Component selectedComponent = newSelection.getContent();
                    if (selectedComponent instanceof ConnectionPanel) {
                        sessionMenu = ((ConnectionPanel) selectedComponent).getSessionMenu();
                    }
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
        if (application.settings.getKeyTabState() == TabState.ALWAYS_OPEN
                || application.settings.getKeyTabState() == TabState.OPEN) {
            setKeyTabVisibility(true);
        }

        if (application.settings.getLogTabState() == TabState.ALWAYS_OPEN
                || application.settings.getLogTabState() == TabState.OPEN) {
            setLogTabVisibility(true);
        }
    }

    public void setKeyTabVisibility(final boolean isVisible) {
        setPanelVisibility(isVisible, keyTab);
    }

    public void setLogTabVisibility(final boolean isVisible) {
        setPanelVisibility(isVisible, logTab);
    }

    private void setPanelVisibility(final boolean isVisible, final Tab tab) {
        final boolean containsTab = tabPanel.containsTab(tab);
        if (isVisible) {
            if (!containsTab) {
                tabPanel.add(tab);
            }
        } else if (containsTab) {
            tabPanel.closeTab(tab);
        }
    }

    public void connect(final AWTProfile profile) {
        final AWTProfile safeProfile = new AWTProfile(profile);
        final SSHTab sshTab = new SSHTab(safeProfile, application, tabPanel);
        tabPanel.add(sshTab);
        sshTab.getContent().connect();
        sshTab.getContent().takeFocus();
    }

    public void connectToCustomProfile() {
        final ConnectDialog connectDialog = new ConnectDialog(this);
        final AWTProfile profile = connectDialog.createProfile();
        connectDialog.dispose();
        if (profile != null) {
            connect(profile);
        }
    }

    public void processArguments(final String[] args) {
        SwingUtils.runInSwingThread(new Runnable() {
            @Override
            public void run() {
                new ArgumentParser<>(new ArgumentParserCallback<AWTProfile>() {
                    @Override
                    public void openConnection(final AWTProfile profile) {
                        connect(profile);
                    }

                    @Override
                    public void reportArgumentError(final String[] errors) {
                        final StringBuilder builder = new StringBuilder();
                        for (String error : errors) {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(error);
                        }

                        SwingUtils.showMessage(MainWindow.this, "<html>"
                                + t("errors.arguments", "Could not process argument:")
                                + "<br />" + builder.toString() + "</html>",
                                t("errors.arguments.title", "Error"),
                                JOptionPane.ERROR_MESSAGE);
                    }

                    @Override
                    public void loadKey(final String path) {
                        application.keyManager.loadKey(path);
                    }
                }, application.settings).processArguments(args);
            }});
    }

    public void split(final SplitDirection splitDirection) {
        tabPanel.split(splitDirection);
    }
}
