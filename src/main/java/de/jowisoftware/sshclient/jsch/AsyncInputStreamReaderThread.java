package de.jowisoftware.sshclient.jsch;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;

import de.jowisoftware.sshclient.debug.PerformanceLogger;
import de.jowisoftware.sshclient.debug.PerformanceType;
import de.jowisoftware.sshclient.util.StringUtils;

public class AsyncInputStreamReaderThread extends Thread {
    private static final Logger LOGGER = Logger
            .getLogger(AsyncInputStreamReaderThread.class);
    private final Channel channel;
    private final InputStreamEvent callback;

    public AsyncInputStreamReaderThread(final Channel channel,
            final InputStreamEvent callback) {
        super("AsyncReader-" + channel.getId());
        this.channel = channel;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            processInputStream();
        } catch (final IOException e) {
            LOGGER.warn("Exception while reading from socket", e);
        }

        callback.streamClosed(channel.getExitStatus());
        channel.disconnect();
        LOGGER.info("Thread ended");
    }

    private void processInputStream() throws IOException {
        final byte[] buffer = new byte[1024];
        final InputStream stream = channel.getInputStream();

        while (channel.isConnected()) {
            processStreamContent(stream, buffer);
        }
        IOUtils.closeQuietly(stream);
    }

    private void processStreamContent(final InputStream stream, final byte[] buffer)
            throws IOException {
        final int read = stream.read(buffer);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Got " + read + " bytes: " +
                    StringUtils.escapeForLogs(buffer, 0, read));
        }

        try {
            PerformanceLogger.start(PerformanceType.REVEICE_CHAR_TO_RENDER);
            callback.gotChars(buffer, read);
        } catch(final RuntimeException e) {
            LOGGER.error("Reader thread catched exception", e);
        }
    }
}
