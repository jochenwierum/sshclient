package de.jowisoftware.sshclient.ui.terminal;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import de.jowisoftware.sshclient.terminal.Profile;

public final class AWTProfile implements Profile<AWTGfxInfo> {
    private String user = System.getProperty("user.name");
    private String host = "localhost";
    private int port = 22;
    private int timeout = 10000;

    private boolean agentForwarding;
    private boolean xForwarding;
    private String x11Host = "127.0.0.1";
    private int x11Display = 0;

    private String command = "";

    private String charsetName = "UTF-8";
    private transient Charset charset;

    private final AWTGfxInfo gfxInfo;
    private final HashMap<String, String> environmentMap;

    private CloseTabMode closeTabMode = CloseTabMode.NO_ERROR;


    public AWTProfile() {
        gfxInfo = new AWTGfxInfo();
        environmentMap = new HashMap<String, String>();
    }

    public AWTProfile(final AWTProfile copy) {
        gfxInfo = new AWTGfxInfo(copy.gfxInfo);
        environmentMap = new HashMap<String, String>(copy.environmentMap);

        user = copy.user;
        host = copy.host;
        port = copy.port;
        timeout = copy.timeout;
        agentForwarding = copy.agentForwarding;
        xForwarding = copy.xForwarding;
        x11Host = copy.x11Host;
        x11Display = copy.x11Display;
        charsetName = copy.charsetName;
        charset = copy.charset;
        closeTabMode = copy.closeTabMode;
        command = copy.command;
    }

    @Override
    public String getDefaultTitle() {
        return user + "@" + host + ":" + port;
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
    public boolean getX11Forwarding() {
        return xForwarding;
    }

    @Override
    public void setX11Forwarding(final boolean forward) {
        xForwarding = forward;
    }

    @Override
    public boolean getAgentForwarding() {
        return agentForwarding;
    }

    @Override
    public void setAgentForwarding(final boolean forward) {
        agentForwarding = forward;
    }

    @Override
    public String getX11Host() {
        return x11Host;
    }

    @Override
    public int getX11Display() {
        return x11Display ;
    }

    @Override
    public void setX11Host(final String x11Host) {
        this.x11Host = x11Host;
    }

    @Override
    public void setX11Display(final int x11Display) {
        this.x11Display = x11Display;
    }

    public void setCloseTabMode(final CloseTabMode newCloseTabMode) {
        this.closeTabMode = newCloseTabMode;
    }

    public CloseTabMode getCloseTabMode() {
        return closeTabMode;
    }

    @Override
    public boolean hasCommand() {
        return !command.trim().isEmpty();
    }

    @Override
    public String getCommand() {
        return command.trim();
    }

    @Override
    public void setCommand(final String command) {
        this.command = command;
    }
}
