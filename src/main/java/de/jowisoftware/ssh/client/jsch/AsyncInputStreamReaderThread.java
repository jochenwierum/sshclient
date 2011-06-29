package de.jowisoftware.ssh.client.jsch;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;

import de.jowisoftware.ssh.client.util.StringUtils;

public class AsyncInputStreamReaderThread extends Thread {
    private static final Logger LOGGER = Logger
            .getLogger(AsyncInputStreamReaderThread.class);
    private final Callback callback;
    private final Channel channel;

    public AsyncInputStreamReaderThread(final Channel channel,
            final Callback callback) {
        this.channel = channel;
        this.callback = callback;
    }

    @Override
    public void run() {
        final byte[] buffer = new byte[1024];
        InputStream stream = null;
        try {
            stream = channel.getInputStream();
            while (channel.isConnected()) {
                while (stream.available() > 0) {
                    final int read = stream.read(buffer);
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Got " + read + " bytes: " +
                                StringUtils.escapeForLogs(buffer, 0, read));
                    }
                    callback.gotChars(buffer, read);
                }

                try {
                    Thread.sleep(80);
                } catch (final InterruptedException e) {
                }
            }
        } catch (final IOException e) {
        }
        IOUtils.closeQuietly(stream);
        LOGGER.info("Thread ended");
    }

    public static interface Callback {
        void gotChars(byte[] buffer, int read);
    }
}
