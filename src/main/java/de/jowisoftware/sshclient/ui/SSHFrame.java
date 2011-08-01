package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.jowisoftware.sshclient.jsch.AsyncInputStreamReaderThread;
import de.jowisoftware.sshclient.jsch.UserInfo;
import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.VisualFeedback;

public class SSHFrame extends JPanel {
    private static final long serialVersionUID = 7873084199411017370L;

    private static final Logger LOGGER = Logger.getLogger(SSHFrame.class);
    private final Profile profile;
    private final JFrame parent;
    private final JSch jsch;

    private ChannelShell channel;
    private Session session;
    private OutputStream outputStream = null;
    private SSHConsole console = null;

    private SSHTabComponent recentTabComponent;
    private final SessionMenu sessionMenu;

    public SSHFrame(final JFrame parent, final Profile profile, final JSch jsch) {
        this.profile = profile;
        this.parent = parent;
        this.jsch = jsch;

        setLayout(new BorderLayout());
        setContent(new InfoPane("Connecting..."));

        console = new SSHConsole(profile);
        sessionMenu = new SessionMenu(console);
        registerListener();
    }

    private void registerListener() {
        console.getSession().getVisualFeedback().add(new VisualFeedback() {
            @Override
            public void setTitle(final String title) {
                recentTabComponent.updateLabel(title);
            }

            @Override
            public void setDisplayType(final DisplayType displayType) {
                sessionMenu.updateMenuStates();
            }

            @Override public void bell() { /* ignored */ }
        });
    }

    public void connect() {
        try {
            connectToServer();
            setContent(console);
            sessionMenu.updateMenuStates();
        } catch(final Exception e) {
            close();
            console = null;
            setContent(new ErrorPane(t("errors.could_not_establish_connection",
                    "Could not establish connection"), e));
            LOGGER.error("Could not connect", e);
            return;
        }
    }

    private void setContent(final JComponent comp) {
        removeAll();
        add(comp, BorderLayout.CENTER);
    }

    public void close() {
        removeAll();
        if (console != null) {
            console.dispose();
            console = null;
        }

        if (outputStream != null) {
            IOUtils.closeQuietly(outputStream);
            outputStream = null;
        }

        if (channel != null) {
            channel.disconnect();
            channel = null;
        }

        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    private void connectToServer() throws JSchException, IOException {
        LOGGER.warn("Connecting to " + profile.getTitle());

        setupSession();
        setupPty();
        channel.connect();
        setupConsole();

        LOGGER.warn("Connected to " + profile.getTitle());
    }

    private void setupConsole() throws IOException {
        new AsyncInputStreamReaderThread(channel, console).start();
        outputStream = channel.getOutputStream();
        console.setOutputStream(outputStream);
        console.setChannel(channel);
    }

    private void setupSession() throws JSchException {
        session = jsch.getSession(
                profile.getUser(), profile.getHost(), profile.getPort());
        session.setUserInfo(new UserInfo(parent));
        session.connect(profile.getTimeout());
        channel = (ChannelShell) session.openChannel("shell");
    }

    private void setupPty() {
        for (final Entry<String, String> env : profile.getEnvironment().entrySet()) {
            channel.setEnv(env.getKey(), env.getValue());
        }

        channel.setEnv("TERM", "xterm");
        channel.setPtyType("xterm");
        channel.setPty(true);
    }

    public void redraw() {
        if (console != null) {
            console.redrawConsole();
        }
    }

    public JComponent createTabComponent(final JTabbedPane pane) {
        recentTabComponent = new SSHTabComponent(this, profile, pane);
        return recentTabComponent.create();
    }

    public void takeFocusWithKey(final KeyEvent e) {
        if (console != null) {
            console.takeFocusWithKey(e);
        }
    }

    public SessionMenu getSessionMenu() {
        return sessionMenu;
    }
}