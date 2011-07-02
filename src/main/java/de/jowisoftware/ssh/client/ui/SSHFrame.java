package de.jowisoftware.ssh.client.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.jowisoftware.ssh.client.ConnectionInfo;
import de.jowisoftware.ssh.client.jsch.AsyncInputStreamReaderThread;
import de.jowisoftware.ssh.client.jsch.UserInfo;

public class SSHFrame extends JPanel  {
    private static final long serialVersionUID = 7873084199411017370L;

    private static final Logger LOGGER = Logger.getLogger(SSHFrame.class);
    private final ConnectionInfo info;

    private Channel channel;
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
            connect(jsch, console);
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

    private void connect(final JSch jsch, final SSHConsole console) {
        LOGGER.warn("Connecting to " + info.getTitle());

        try {
            session = jsch.getSession(
                    info.getUser(), info.getHost(), info.getPort());
            session.setUserInfo(new UserInfo(parent));
            session.connect(info.getTimeout());
            channel = session.openChannel("shell");
            channel.connect();
            new AsyncInputStreamReaderThread(channel, console).start();
            outputStream = channel.getOutputStream();
            console.setOutputStream(outputStream);
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
