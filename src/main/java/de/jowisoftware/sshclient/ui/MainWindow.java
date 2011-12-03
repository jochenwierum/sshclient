package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import de.jowisoftware.sshclient.i18n.Translation;
import de.jowisoftware.sshclient.log.LogPanel;
import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.settings.ApplicationSettings;
import de.jowisoftware.sshclient.settings.ApplicationSettings.TabState;
import de.jowisoftware.sshclient.settings.KeyAgentManager;
import de.jowisoftware.sshclient.settings.persisting.XMLLoader;
import de.jowisoftware.sshclient.settings.persisting.XMLPersister;
import de.jowisoftware.sshclient.ui.security.PasswordManager;
import de.jowisoftware.sshclient.ui.settings.ConnectDialog;
import de.jowisoftware.sshclient.util.FontUtils;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = -2951599770927217249L;
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class);

    public final ApplicationSettings settings = new ApplicationSettings();

    private final JSch jsch = new JSch();
    private final KeyAgentManager keyManager = new KeyAgentManager(jsch, settings);

    private final PrivateKeyTab keyPanel = new PrivateKeyTab(jsch, keyManager);
    private final JComponent logPanel = new LogPanel();

    private final MainWindowMenu menu = new MainWindowMenu(this);
    private final MainWindowToolbar toolBar = new MainWindowToolbar(this);

    private final JTabbedPane pane = createTabbedPane();
    private final Timer timer = createTimer(pane);
    public final PasswordManager passwordManager = new PasswordManager(this,
            settings.getPasswordStorage());

    private final File projectDir;

    public MainWindow() {
        FontUtils.fillAsyncCache();

        projectDir = prepareProjectDir();
        final File settingsFile = new File(projectDir, "settings.xml");
        if (settingsFile.isFile()) {
            new XMLLoader(settings).load(settingsFile);
            toolBar.updateProfiles();
        }

        initTranslation();
        setTitle("SSH");

        try {
            initJSch();
        } catch(final JSchException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "SSH",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            throw new RuntimeException(e);
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initWindowElements();
    }

    private void initTranslation() {
        final String language = settings.getLanguage();
        Translation.initStaticTranslationWithLanguage(language);
    }

    private Timer createTimer(final JTabbedPane updatePane) {
        final Timer newTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component component = updatePane.getSelectedComponent();
                if (component instanceof ConnectionFrame) {
                    ((ConnectionFrame) component).redraw();
                }
            }
        });
        newTimer.setRepeats(true);
        newTimer.start();
        return newTimer;
    }

    @Override
    public void dispose() {
        // TODO: if state is connecting, we get a problem here

        persistTabStates();

        timer.stop();
        while(pane.getTabCount() > 0) {
            final Component component = pane.getComponentAt(0);
            if (component instanceof ConnectionFrame) {
                final ConnectionFrame tab = (ConnectionFrame) component;
                tab.close();
            }
            pane.removeTabAt(0);
        }

        try {
            new XMLPersister(settings).save(
                    new File(projectDir, "settings.xml"));
        } catch(final RuntimeException e) {
            LOGGER.error("Could not save settings", e);
        }
        super.dispose();
    }

    private void persistTabStates() {
        if (pane.indexOfComponent(keyPanel) >= 0
                && settings.getKeyTabState() == TabState.CLOSED) {
            settings.setKeyTabState(TabState.OPENED);
        } else if (pane.indexOfComponent(keyPanel) == -1
                && settings.getKeyTabState() == TabState.OPENED) {
            settings.setKeyTabState(TabState.CLOSED);
        }

        if (pane.indexOfComponent(logPanel) >= 0
                && settings.getLogTabState() == TabState.CLOSED) {
            settings.setLogTabState(TabState.OPENED);
        } else if (pane.indexOfComponent(logPanel) == -1
                && settings.getLogTabState() == TabState.OPENED) {
            settings.setLogTabState(TabState.CLOSED);
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
        setVisible(true);
    }

    private JTabbedPane createTabbedPane() {
        final JTabbedPane tabbedPane = new DnDTabbedPane();
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
        if (settings.getKeyTabState() == TabState.ALYWAYS_OPEN
                || settings.getKeyTabState() == TabState.OPENED) {
            setKeyTabVisibility(true);
        }

        if (settings.getLogTabState() == TabState.ALYWAYS_OPEN
                || settings.getLogTabState() == TabState.OPENED) {
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

    private void initJSch() throws JSchException {
        JSch.setLogger(new de.jowisoftware.sshclient.jsch.JschLogger());
        jsch.setKnownHosts(new File(projectDir, "known_hosts").getAbsolutePath());
        keyManager.loadKeyListFromSettings();

        final File privKey = new File(projectDir, "id_rsa");
        if (privKey.isFile()) {
            keyManager.loadKey(privKey.getAbsolutePath());
        }
    }

    private File prepareProjectDir() {
        final File home = new File(System.getProperty("user.home"));

        final File finalProjectDir = new File(home, ".ssh");
        if (finalProjectDir.isDirectory()) {
            if (!finalProjectDir.exists()) {
                if(!finalProjectDir.mkdir()) {
                    throw new RuntimeException("Could not create directory: " +
                            finalProjectDir.getAbsolutePath());
                }
            }
        }
        return finalProjectDir;
    }

    public void connect(final AWTProfile profile) {
        final AWTProfile safeProfile = (AWTProfile) profile.clone();
        final ConnectionFrame sshFrame = new ConnectionFrame(this, safeProfile,
        	passwordManager, jsch);
        pane.addTab(safeProfile.getDefaultTitle(), sshFrame);
        pane.setTabComponentAt(pane.getTabCount() - 1,
                sshFrame.createTabComponent(pane));
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

    public void updateProfiles() {
        toolBar.updateProfiles();
    }
}
