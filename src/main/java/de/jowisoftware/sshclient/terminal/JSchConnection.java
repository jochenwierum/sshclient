package de.jowisoftware.sshclient.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.jowisoftware.sshclient.jsch.AsyncInputStreamReaderThread;
import de.jowisoftware.sshclient.jsch.InputStreamEvent;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;

public class JSchConnection {
    private static final Logger LOGGER = Logger.getLogger(JSchConnection.class);
    private final int X11_BASE_PORT = 6000;

    private final JSch jsch;
    private final SSHUserInfo userInfo;
    private final Profile<?> profile;
    private final InputStreamEvent callback;

    private Session session;
    private ChannelShell channel;
    private OutputStream outputStream;

    public JSchConnection(final JSch jsch, final Profile<?> profile,
            final SSHUserInfo userInfo, final InputStreamEvent callback) {
        this.profile = profile;
        this.jsch = jsch;
        this.callback = callback;
        this.userInfo = userInfo;
    }

    public void connect() throws JSchException, IOException {
        checkState();
        LOGGER.warn("Connecting to " + profile.getDefaultTitle());

        openSession();
        openChannel();
        setupStreams();

        LOGGER.warn("Connected to " + profile.getDefaultTitle());
    }

    private void openChannel() throws JSchException {
        channel = createChannel();
        setupChannelForwardings();
        connectChannel();
    }

    private void openSession() throws JSchException {
        session = createSession();
        setupSessionForwardings();
        connectedSession();
    }

    private void setupSessionForwardings() {
        if (profile.getX11Forwarding()) {
            session.setX11Host(profile.getX11Host());
            session.setX11Port(profile.getX11Display() + X11_BASE_PORT);
        }
    }

    private void connectChannel() throws JSchException {
        channel.connect();
    }

    private void setupChannelForwardings() {
        channel.setXForwarding(profile.getX11Forwarding());
        channel.setAgentForwarding(profile.getAgentForwarding());
    }

    private void checkState() {
        if (callback == null) {
            throw new IllegalStateException("Callback not set");
        }

        if (userInfo == null) {
            throw new IllegalStateException("UserInfo not set");
        }
    }

    private void setupStreams() throws IOException {
        new AsyncInputStreamReaderThread(channel, callback).start();
        outputStream = channel.getOutputStream();
    }

    private void connectedSession() throws JSchException {
        session.connect(profile.getTimeout());
    }

    private Session createSession() throws JSchException {
        final Session sshSession = jsch.getSession(
                profile.getUser(), profile.getHost(), profile.getPort());
        sshSession.setUserInfo(userInfo);
        return sshSession;
    }

    private ChannelShell createChannel() throws JSchException {
        final ChannelShell shellChannel = (ChannelShell) session.openChannel("shell");

        for (final Entry<String, String> env : profile.getEnvironment().entrySet()) {
            shellChannel.setEnv(env.getKey(), env.getValue());
        }

        shellChannel.setEnv("TERM", "xterm");
        shellChannel.setPtyType("xterm");
        shellChannel.setPty(true);

        return shellChannel;
    }

    public void close() {
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

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public ChannelShell getChannel() {
        return channel;
    }
}
