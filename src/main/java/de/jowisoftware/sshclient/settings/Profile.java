package de.jowisoftware.sshclient.settings;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jowisoftware.sshclient.ui.terminal.GfxInfo;

public class Profile {
    private String user = System.getProperty("user.name");
    private String host = "localhost";
    private int port = 22;
    private int timeout = 10000;
    private Charset charset = Charset.forName("UTF-8");
    private GfxInfo gfxInfo = new GfxInfo();
    private final HashMap<String, String> environmentMap = new HashMap<String, String>();

    public String getDefaultTitle() {
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
        this.charset = charset;
    }

    public GfxInfo getGfxSettings() {
        return gfxInfo;
    }

    public Map<String, String> getEnvironment() {
        return environmentMap;
    }

    @Override
    public Object clone() {
        final Profile p = new Profile();
        p.charset = charset;
        p.host = host;
        p.port = port;
        p.timeout = timeout;
        p.user = user;
        p.gfxInfo = (GfxInfo) gfxInfo.clone();
        for (final Entry<String, String> entry : environmentMap.entrySet()) {
            p.environmentMap.put(entry.getKey(), entry.getValue());
        }
        return p;
    }
}
