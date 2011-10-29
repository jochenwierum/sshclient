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
    private final Profile<?> profile;
    private ChannelShell channel;
    private final InputStreamEvent callback;
    private OutputStream outputStream;
    private final JSch jsch;
    private final SSHUserInfo userInfo;
    private Session session;

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

        connectSession();
        connectChannel();
        setupStreams();

        LOGGER.warn("Connected to " + profile.getDefaultTitle());
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

    private void connectSession() throws JSchException {
        session = jsch.getSession(
                profile.getUser(), profile.getHost(), profile.getPort());
        session.setUserInfo(userInfo);
        session.connect(profile.getTimeout());
    }

    private void connectChannel() throws JSchException {
        channel = (ChannelShell) session.openChannel("shell");

        for (final Entry<String, String> env : profile.getEnvironment().entrySet()) {
            channel.setEnv(env.getKey(), env.getValue());
        }

        channel.setEnv("TERM", "xterm");
        channel.setPtyType("xterm");
        channel.setPty(true);

        channel.connect();
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
