package de.jowisoftware.sshclient.proxy;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Session;

public class SocksServer extends Thread {
    private static final Logger LOGGER = Logger.getLogger(SocksServer.class);

    private final Session session;

    private final int port;
    private volatile boolean running = true;

    public SocksServer(final Session session, final int port) {
        this.session = session;
        this.port = port;

        setName("SOCKS-" + port);
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        LOGGER.debug("Starting SOCKS proxy server on port " + port);

        try (ServerSocket socket = new ServerSocket(port, 10,
                InetAddress.getLoopbackAddress())) {
            socket.setSoTimeout(500);
            while (running) {
                try {
                    final Socket clientSocket = socket.accept();

                    new SocksConnection(clientSocket, session)
                            .start();
                } catch (final SocketTimeoutException e) {
                    // this exception is totally fine - we use it to check
                    // whether the thread should be stopped
                }
            }

            LOGGER.debug("Closing SOCKS socket on port " + port);
        } catch (final Exception e) {
            LOGGER.error("Error while serving SOCKS proxy on port " + port, e);
        }
    }
}
