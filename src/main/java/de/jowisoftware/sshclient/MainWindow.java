package de.jowisoftware.sshclient;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import de.jowisoftware.sshclient.log.LogPanel;
import de.jowisoftware.sshclient.settings.ConnectionInfo;
import de.jowisoftware.sshclient.settings.KeyAgentManager;
import de.jowisoftware.sshclient.ui.DnDTabbedPane;
import de.jowisoftware.sshclient.ui.PrivateKeyTab;
import de.jowisoftware.sshclient.ui.SSHFrame;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = -2951599770927217249L;
    private JTabbedPane pane;
    private JSch jsch;
    private Timer timer;
    private Component logPanel;
    private PrivateKeyTab privateKeyTab;

    public MainWindow() {
        super("SSH");

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
                // TODO: repaint only visible pane?
                for (int i = 0; i < pane.getTabCount(); ++i) {
                    final Component component = pane.getComponent(i);
                    if (component instanceof SSHFrame) {
                        ((SSHFrame) component).redraw();
                    }
                }
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    public void dispose() {
        // TODO: if state is connecting, we get a problem here
        timer.stop();
        while(pane.getTabCount() > 0) {
            final Component component = pane.getComponentAt(0);
            if (component instanceof SSHFrame) {
                final SSHFrame tab = (SSHFrame) component;
                tab.close();
            }
            pane.removeTabAt(0);
        }
        super.dispose();
    }

    private void initWindowElements() {
        final JMenuBar menu = new JMenuBar();
        final JMenu fileMenu = createFileMenu();
        final JMenu viewMenu = createViewMenu();
        menu.add(fileMenu);
        menu.add(viewMenu);

        pane = createPane();

        setJMenuBar(menu);
        add(pane);

        initTabs();

        setSize(640, 480);
        setVisible(true);
    }

    private JTabbedPane createPane() {
        final JTabbedPane tabbedPane = new DnDTabbedPane();
        tabbedPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                if (pane.getSelectedComponent() instanceof SSHFrame) {
                    ((SSHFrame) pane.getSelectedComponent())
                            .takeFocusWithKey(e);
                }
            }
        });

        return tabbedPane;
    }

    private void initTabs() {
        privateKeyTab = new PrivateKeyTab(jsch);
        logPanel = new LogPanel();
    }

    private void initJSch() throws JSchException {
        JSch.setLogger(new de.jowisoftware.sshclient.jsch.JschLogger());
        jsch = new JSch();

        final File home = new File(System.getProperty("user.home"));
        final File projectDir = new File(home, ".ssh");
        if (home.isDirectory()) {
            if (!projectDir.exists()) {
                projectDir.mkdir();
            }
            jsch.setKnownHosts(new File(projectDir, "known_hosts").getAbsolutePath());
        }

        final File keyListFile = new File(projectDir, "keyagent");
        if (keyListFile.isFile()) {
            new KeyAgentManager(jsch).loadKeyListFromFile(keyListFile);
        }

        final File privKey = new File(projectDir, "id_rsa");
        if (privKey.isFile()) {
            jsch.addIdentity(privKey.getAbsolutePath());
        }
    }

    // TODO replace this through a real login
    private void createConnection() {
        final ConnectionInfo info = new ConnectionInfo();
        final String result =
            JOptionPane.showInputDialog("[user@]host[:port]", "jwieru2s@home.inf.h-brs.de");
        if (result == null) {
            return;
        }

        final Matcher matcher = Pattern.compile("([^@]*)@([^:]+)(?:(:\\d+))?").matcher(result);
        if (!matcher.matches()) {
            return;
        }

        if (matcher.group(1) != null) {
            info.setUser(matcher.group(1));
        }
        if (matcher.group(2) != null) {
            info.setHost(matcher.group(2));
        }
        if (matcher.group(3) != null) {
            info.setPort(Integer.parseInt(matcher.group(3)));
        }

        final SSHFrame sshFrame = new SSHFrame(this, info, jsch);
        pane.addTab(info.getTitle(), sshFrame);
        pane.setTabComponentAt(pane.getTabCount() - 1,
                sshFrame.createTabComponent(pane));
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
        final JCheckBoxMenuItem entry = new JCheckBoxMenuItem("Show Logs");
        entry.setMnemonic('l');

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (entry.isSelected()) {
                    pane.addTab("log", logPanel);
                } else {
                    for (int i = 0; i < pane.getTabCount(); ++i) {
                        if (pane.getComponentAt(i) == logPanel) {
                            pane.removeTabAt(i);
                        }
                    }
                }
            }
        });

        return entry;
    }

    private JMenuItem createKeyAgentVisibilityEntry() {
        final JCheckBoxMenuItem entry = new JCheckBoxMenuItem("Key Agent");
        entry.setMnemonic('a');

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (entry.isSelected()) {
                    pane.addTab("Keys", privateKeyTab);
                } else {
                    for (int i = 0; i < pane.getTabCount(); ++i) {
                        if (pane.getComponentAt(i) == privateKeyTab) {
                            pane.removeTabAt(i);
                        }
                    }
                }
            }
        });

        return entry;
    }

    private JMenuItem createConnectEntry() {
        final JMenuItem entry = new JMenuItem("Connect...");
        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                createConnection();
            }
        });
        return entry;
    }

    private JMenuItem createQuitMenuEntry() {
        final JMenuItem entry = new JMenuItem("Quit");
        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        });
        return entry;
    }
}
