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
        int exitStatus = channel.getExitStatus();
        try {
            exitStatus = processInputStream();
        } catch (final IOException e) {
            exitStatus = Integer.MAX_VALUE;
            LOGGER.warn("Exception while reading from socket", e);
        }

        callback.streamClosed(exitStatus);
        channel.disconnect();
        LOGGER.info("Thread ended, exit status: " + exitStatus);
    }

    private int processInputStream() throws IOException {
        final InputStream stream = channel.getInputStream();

        while (channel.isConnected() && processStreamContent(stream)) {
        }

        final int exitStatus = channel.getExitStatus();
        IOUtils.closeQuietly(stream);

        return exitStatus;
    }

    private boolean processStreamContent(final InputStream stream)
            throws IOException {
        final byte[] buffer = new byte[1024];
        final int read = stream.read(buffer);

        if (read == -1) {
            return false;
        }

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

        return true;
    }
}
