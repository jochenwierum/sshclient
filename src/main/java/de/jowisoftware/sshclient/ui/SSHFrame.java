package de.jowisoftware.sshclient.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.jowisoftware.sshclient.ConnectionInfo;
import de.jowisoftware.sshclient.jsch.AsyncInputStreamReaderThread;
import de.jowisoftware.sshclient.jsch.UserInfo;

public class SSHFrame extends JPanel {
    private static final long serialVersionUID = 7873084199411017370L;

    private static final Logger LOGGER = Logger.getLogger(SSHFrame.class);
    private final ConnectionInfo info;

    private ChannelShell channel;
    private Session session;
    private OutputStream outputStream = null;
    private JFrame parent;
    private SSHConsole console = null;

    public SSHFrame(final JFrame parent, final ConnectionInfo info, final JSch jsch) {
        this.info = info;
        this.parent = parent;

        setLayout(new BorderLayout());

        try {
            console = new SSHConsole(info);
            connect(jsch);
            add(console, BorderLayout.CENTER);
        } catch(final Exception e) {
            close();
            console = null;
            removeAll();
            add(new ErrorPane("Could not establish connection", e), BorderLayout.CENTER);
            LOGGER.error("Could not connect", e);
            return;
        }
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

    private void connect(final JSch jsch) {
        LOGGER.warn("Connecting to " + info.getTitle());

        try {
            session = jsch.getSession(
                    info.getUser(), info.getHost(), info.getPort());
            session.setUserInfo(new UserInfo(parent));
            session.connect(info.getTimeout());
            channel = (ChannelShell) session.openChannel("shell");
            // TODO: Make env settable via config
            channel.setEnv("TERM", "xterm");
            channel.setPtyType("xterm");
            channel.setPty(true);
            channel.connect();
            new AsyncInputStreamReaderThread(channel, console).start();
            outputStream = channel.getOutputStream();
            console.setOutputStream(outputStream);
            console.setChannel(channel);
        } catch (final IOException e) {
            LOGGER.error("Could not estabish connection", e);
            throw new RuntimeException(e);
        } catch (final JSchException e) {
            LOGGER.error("Could not estabish connection", e);
            throw new RuntimeException(e);
        }

        LOGGER.warn("Connected to " + info.getTitle());
    }

    public void redraw() {
        if (console != null) {
            console.redrawConsole();
        }
    }
}
