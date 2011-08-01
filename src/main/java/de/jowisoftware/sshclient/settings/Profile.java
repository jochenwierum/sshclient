package de.jowisoftware.sshclient.settings;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import de.jowisoftware.sshclient.ui.GfxInfo;

public class Profile {
    private String user = System.getProperty("user.name");
    private String host = "localhost";
    private int port = 22;
    private int timeout = 10000;
    private Charset charset = Charset.forName("UTF-8");
    private final GfxInfo gfxInfo = new GfxInfo();

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
        return charset;
    }

    public void setCharset(final Charset charset) {
        this.charset  = charset;
    }

    public GfxInfo getGfxSettings() {
        return gfxInfo;
    }

    public Map<String, String> getEnvironment() {
        return new HashMap<String, String>();
    }
}
