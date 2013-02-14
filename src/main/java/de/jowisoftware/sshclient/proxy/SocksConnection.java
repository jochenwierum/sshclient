package de.jowisoftware.sshclient.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelDirectTCPIP;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SocksConnection extends Thread {
    private static final Logger LOGGER = Logger
            .getLogger(SocksConnection.class);

    private final DefaultSocksInitialisationProcessor processor =
            new DefaultSocksInitialisationProcessor();

    private final Socket socket;
    private final Session session;
    private Channel channel;

    public SocksConnection(final Socket clientSocket, final Session session) {
        this.socket = clientSocket;
        this.session = session;

        setName("SOCKS-" + socket.getLocalPort()
                + "-" + socket.getInetAddress().toString() + ":"
                + socket.getPort());
    }

    @Override
    public void run() {
        final String remoteSocket = socket.getInetAddress().toString() + ":"
                + socket.getPort();

        LOGGER.info("Starting socks thread for " + remoteSocket);

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            establishConnection(inputStream, outputStream);

            // The streams are not closed here because they are forwarded to
            // JSCHs Channel's thread. The thread closes the Streams when
            // the connection is closed.
        } catch (final IOException | JSchException e) {
            LOGGER.info("Error while initializing proxy for " + remoteSocket,
                    e);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private void establishConnection(final InputStream inputStream,
            final OutputStream outputStream) throws IOException, JSchException {
        int inputByte = inputStream.read();
        while (inputByte != -1 && !processor.isFinished()) {
            final byte[] answer = processor.process((byte) inputByte);
            outputStream.write(answer);

            if (processor.isFinished()) {
                LOGGER.info("Establishing connection to " + processor.getHost()
                        + ":" + processor.getPort());
                channel = session.openChannel("direct-tcpip");
                ((ChannelDirectTCPIP) channel).setHost(processor.getHost());
                ((ChannelDirectTCPIP) channel).setPort(processor.getPort());
                ((ChannelDirectTCPIP) channel).setInputStream(inputStream);
                ((ChannelDirectTCPIP) channel).setOutputStream(outputStream);
                channel.connect();
            } else {
                inputByte = inputStream.read();
            }
        }
    }
}
