package de.jowisoftware.sshclient.settings;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jowisoftware.sshclient.terminal.Profile;
import de.jowisoftware.sshclient.ui.terminal.AWTGfxInfo;

public final class AWTProfile implements Profile<AWTGfxInfo>, Cloneable {
    private String user = System.getProperty("user.name");
    private String host = "localhost";
    private int port = 22;
    private int timeout = 10000;

    private String charsetName = "UTF-8";
    private transient Charset charset;
    private AWTGfxInfo gfxInfo = new AWTGfxInfo();
    private HashMap<String, String> environmentMap = new HashMap<String, String>();

    @Override
    public String getDefaultTitle() {
        return user + "@" + host;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setUser(final String user) {
        this.user = user;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(final String host) {
        this.host = host;
    }

    @Override
    public void setPort(final int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setTimeout(final int timeout) {
        this.timeout  = timeout;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public Charset getCharset() {
        if (charset == null) {
            if (charsetName != null) {
                charset = Charset.forName(charsetName);
            }
        }
        return charset;
    }

    @Override
    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    public String getCharsetName() {
        return charsetName;
    }

    @Override
    public AWTGfxInfo getGfxSettings() {
        return gfxInfo;
    }

    @Override
    public Map<String, String> getEnvironment() {
        return environmentMap;
    }

    @Override
    public Object clone() {
        AWTProfile clone;
        try {
            clone = (AWTProfile) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        clone.gfxInfo = (AWTGfxInfo) gfxInfo.clone();

        clone.environmentMap = new HashMap<String, String>();
        for (final Entry<String, String> entry : environmentMap.entrySet()) {
            clone.environmentMap.put(entry.getKey(), entry.getValue());
        }

        return clone;
    }
}
