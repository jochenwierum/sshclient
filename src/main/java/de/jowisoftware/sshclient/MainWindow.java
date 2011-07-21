package de.jowisoftware.sshclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import de.jowisoftware.sshclient.log.LogPanel;
import de.jowisoftware.sshclient.settings.ApplicationSettings;
import de.jowisoftware.sshclient.settings.ApplicationSettings.TabState;
import de.jowisoftware.sshclient.settings.KeyAgentManager;
import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.settings.XMLLoader;
import de.jowisoftware.sshclient.settings.XMLPersister;
import de.jowisoftware.sshclient.ui.ClosableTabComponent;
import de.jowisoftware.sshclient.ui.DnDTabbedPane;
import de.jowisoftware.sshclient.ui.MainWindowMenu;
import de.jowisoftware.sshclient.ui.MainWindowToolbar;
import de.jowisoftware.sshclient.ui.PrivateKeyTab;
import de.jowisoftware.sshclient.ui.SSHFrame;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = -2951599770927217249L;
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class);

    private JTabbedPane pane;
    private Timer timer;
    private JComponent logPanel;
    private PrivateKeyTab keyPanel;
    private MainWindowMenu menu;

    public final ApplicationSettings settings = new ApplicationSettings();

    private final File projectDir;
    private JSch jsch;
    private KeyAgentManager keyManager;
    private MainWindowToolbar toolBar;


    public MainWindow() {
        super("SSH");
        projectDir = prepareProjectDir();
        new XMLLoader(settings).load(new File(projectDir, "settings.xml"));

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

        createTimer();
    }

    private void createTimer() {
        timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component component = pane.getSelectedComponent();
                if (component instanceof SSHFrame) {
                    ((SSHFrame) component).redraw();
                }
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    public void dispose() {
        // TODO: if state is connecting, we get a problem here

        persistTabStates();

        timer.stop();
        while(pane.getTabCount() > 0) {
            final Component component = pane.getComponentAt(0);
            if (component instanceof SSHFrame) {
                final SSHFrame tab = (SSHFrame) component;
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
        menu = new MainWindowMenu(this);
        setJMenuBar(menu.getMenuBar());

        setLayout(new BorderLayout());

        pane = createPane();
        add(pane, BorderLayout.CENTER);

        toolBar = new MainWindowToolbar(this);
        add(toolBar.getToolBar(), BorderLayout.NORTH);

        initTabs();

        setSize(640, 480);
        setVisible(true);
    }

    private JTabbedPane createPane() {
        final JTabbedPane tabbedPane = new DnDTabbedPane();
        tabbedPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                final Component selectedComponent = pane.getSelectedComponent();
                if (selectedComponent instanceof SSHFrame) {
                    ((SSHFrame) selectedComponent).takeFocusWithKey(e);
                }
            }
        });

        return tabbedPane;
    }

    private void initTabs() {
        keyPanel = new PrivateKeyTab(jsch, keyManager);
        logPanel = new LogPanel();

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
        setPanelVisibility(isVisible, keyPanel, "keys");
    }

    public void setLogTabVisibility(final boolean isVisible) {
        setPanelVisibility(isVisible, logPanel, "logs");
    }

    private void setPanelVisibility(final boolean isVisible,
            final JComponent panel, final String title) {
        final int tabPos = pane.indexOfComponent(panel);

        if (isVisible) {
            if (tabPos == -1) {
                pane.addTab(title, panel);
                pane.setTabComponentAt(pane.getTabCount() - 1,
                        new ClosableTabComponent(title, panel, pane));
            }
            pane.setSelectedComponent(panel);
        } else if (isVisible && tabPos >= 0) {
            pane.remove(panel);
        }
    }

    private void initJSch() throws JSchException {
        JSch.setLogger(new de.jowisoftware.sshclient.jsch.JschLogger());
        jsch = new JSch();
        keyManager = new KeyAgentManager(jsch, settings);

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
                finalProjectDir.mkdir();
            }
        }
        return finalProjectDir;
    }

    public void connect(final Profile profile) {
        final SSHFrame sshFrame = new SSHFrame(this, profile, jsch);
        pane.addTab(profile.getTitle(), sshFrame);
        pane.setTabComponentAt(pane.getTabCount() - 1,
                sshFrame.createTabComponent(pane));
        pane.setSelectedComponent(sshFrame);
    }

    public void connectToCustomProfile() {
        final Profile profile = new Profile();
        final String result =
            JOptionPane.showInputDialog("[user@]host[:port]", "localhost");
        if (result == null) {
            return;
        }

        final Matcher matcher = Pattern.compile("([^@]*)@([^:]+)(?:(:\\d+))?").matcher(result);
        if (!matcher.matches()) {
            return;
        }

        if (matcher.group(1) != null) {
            profile.setUser(matcher.group(1));
        }
        if (matcher.group(2) != null) {
            profile.setHost(matcher.group(2));
        }
        if (matcher.group(3) != null) {
            profile.setPort(Integer.parseInt(matcher.group(3)));
        }

        connect(profile);
    }
}
