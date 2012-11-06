package de.jowisoftware.sshclient.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.events.EventHubClient;
import de.jowisoftware.sshclient.jsch.AsyncInputStreamReaderThread;
import de.jowisoftware.sshclient.jsch.InputStreamEvent;
import de.jowisoftware.sshclient.jsch.InputStreamEventHub;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;

public class JSchConnection {
    private static final Logger LOGGER = Logger.getLogger(JSchConnection.class);
    private final int X11_BASE_PORT = 6000;

    private final JSch jsch;
    private final SSHUserInfo userInfo;
    private final Profile<?> profile;

    private Session session;
    private Channel channel;
    private OutputStream outputStream;

    private final EventHub<InputStreamEvent> events =
            new InputStreamEventHub();

    public JSchConnection(final JSch jsch, final Profile<?> profile,
            final SSHUserInfo userInfo) {
        this.profile = profile;
        this.jsch = jsch;
        this.userInfo = userInfo;
    }

    public EventHubClient<InputStreamEvent> events() {
        return events;
    }

    public void connect() throws JSchException, IOException {
        LOGGER.warn("Connecting to " + profile.getDefaultTitle());

        openSession();
        openChannel();
        setupStreams();

        LOGGER.warn("Connected to " + profile.getDefaultTitle());
    }

    private void openChannel() throws JSchException {
        channel = createChannel();
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

        for (final Forwarding forwarding : profile.getLocalForwardings()) {
            try {
                session.setPortForwardingL(forwarding.sourceHost, forwarding.sourcePort,
                        forwarding.remoteHost, forwarding.remotePort);
            } catch (final JSchException e) {
                LOGGER.error("Could not setup port forwarding " + forwarding +
                        ", ignoring setup and continuing connection", e);
            }
        }

        for (final Forwarding forwarding : profile.getRemoteForwardings()) {
            try {
                session.setPortForwardingR(forwarding.sourceHost, forwarding.sourcePort,
                        forwarding.remoteHost, forwarding.remotePort);
            } catch (final JSchException e) {
                LOGGER.error("Could not setup port forwarding " + forwarding +
                        ", ignoring setup and continuing connection", e);
            }
        }
    }

    private void connectChannel() throws JSchException {
        channel.connect();
    }

    private void setupChannelForwardings(final ChannelShell shellChannel) {
        shellChannel.setXForwarding(profile.getX11Forwarding());
        shellChannel.setAgentForwarding(profile.getAgentForwarding());
    }

    private void setupStreams() throws IOException {
        final AsyncInputStreamReaderThread streamReader =
                new AsyncInputStreamReaderThread(channel, events.fire());
        streamReader.start();
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

    private Channel createChannel() throws JSchException {
        if (profile.hasCommand()) {
            return createExecChannel();
        } else {
            return createShellChannel();
        }
    }

    private Channel createExecChannel() throws JSchException {
        final ChannelExec execChannel = (ChannelExec) session.openChannel("exec");
        execChannel.setCommand(profile.getCommand());

        execChannel.setEnv("TERM", "xterm");
        execChannel.setPtyType("xterm");
        execChannel.setPty(true);
        return execChannel;
    }

    private Channel createShellChannel() throws JSchException {
        final ChannelShell shellChannel = (ChannelShell) session.openChannel("shell");
        setupChannelForwardings(shellChannel);

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

    public Channel getChannel() {
        return channel;
    }
}
