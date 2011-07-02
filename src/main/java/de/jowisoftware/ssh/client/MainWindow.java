package de.jowisoftware.ssh.client;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import de.jowisoftware.ssh.client.ui.SSHFrame;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = -2951599770927217249L;
    private JTabbedPane pane;
    private JSch jsch;
    private Timer timer;

    public MainWindow() {
        super("SSH");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initWindowElements();

        try {
            initJSch();
        } catch(final JSchException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "SSH",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            throw new RuntimeException(e);
        }

        createConnection();
        createTimer();
    }

    private void createTimer() {
        timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
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
                pane.removeTabAt(0);
            }
        }
        super.dispose();
    }

    private void initWindowElements() {
        final JMenuBar menu = new JMenuBar();
        final JMenu fileMenu = createFileMenu();
        menu.add(fileMenu);

        pane = new JTabbedPane();

        setJMenuBar(menu);
        add(pane);

        setSize(640, 480);
        setVisible(true);
    }

    private void initJSch() throws JSchException {
        JSch.setLogger(new de.jowisoftware.ssh.client.jsch.JschLogger());
        jsch = new JSch();

        final File home = new File(System.getProperty("user.home"));
        if (home.isDirectory()) {
            final File projectDir = new File(home, ".ssh");
            if (!projectDir.exists()) {
                projectDir.mkdir();
            }
            jsch.setKnownHosts(new File(projectDir, "known_hosts").getAbsolutePath());
        }
    }

    private void createConnection() {
        final ConnectionInfo info = new ConnectionInfo(jsch);
        final String result =
            JOptionPane.showInputDialog("user@host", "jwieru2s@home.inf.h-brs.de");
        final String[] parts = result.split("@");
        info.setUser(parts[0]);
        info.setHost(parts[1]);
        //info.setUser("gast");
        //info.setPort(2222);
        //info.setHost("jowisoftware.dyndns.org");
        pane.addTab(info.getTitle(), new SSHFrame(this, info));
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(createQuitMenuEntry());

        return fileMenu;
    }

    private JMenuItem createQuitMenuEntry() {
        final JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        });
        return quit;
    }
}
