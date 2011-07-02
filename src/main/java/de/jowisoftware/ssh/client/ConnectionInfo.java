package de.jowisoftware.ssh.client;

import java.nio.charset.Charset;

import de.jowisoftware.ssh.client.ui.GfxInfo;

public class ConnectionInfo {
    private String user = System.getProperty("user.name");
    private String host = "localhost";
    private int port = 22;
    private int timeout = 10000;

    public String getTitle() {
        return user + "@" + host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setTimeout(final int timeout) {
        this.timeout  = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public Charset getCharset() {
        return Charset.forName("UTF-8");
    }

    public GfxInfo getGfxSettings() {
        return new GfxInfo();
    }
}
