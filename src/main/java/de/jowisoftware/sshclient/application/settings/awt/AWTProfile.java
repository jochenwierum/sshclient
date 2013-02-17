package de.jowisoftware.sshclient.application.settings.awt;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.Persist;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.TraversalType;
import de.jowisoftware.sshclient.terminal.gfx.awt.AWTGfxInfo;
import de.jowisoftware.sshclient.ui.terminal.CloseTabMode;

public final class AWTProfile implements Profile<AWTGfxInfo> {
    @Persist private String user = System.getProperty("user.name");
    @Persist private String host = "localhost";
    @Persist private int port = 22;
    @Persist private int timeout = 10000;
    @Persist private int keepAliveInterval = 10000;
    @Persist private int keepAliveCount = 5;

    @Persist("forwardings/agent") private boolean agentForwarding;
    @Persist("forwardings/x11/@enable") private boolean xForwarding;
    @Persist("forwardings/x11/host") private String x11Host = "127.0.0.1";
    @Persist("forwardings/x11/display") private int x11Display = 0;

    @Persist(value = "forwardings/portforwardings", traversalType = TraversalType.LIST, traverseListAndMapChildrenRecursively = true, targetClass = Forwarding.class)
    private final List<Forwarding> portForwardings;
    @Persist("forwardings/proxyPort") private Integer socksPort = null;

    @Persist private String command = "";

    @Persist("charset") private String charsetName = "UTF-8";
    private transient Charset charset;

    @Persist(traversalType = TraversalType.RECURSIVE)
    private final AWTGfxInfo gfxInfo;

    @Persist(value = "environment", traversalType = TraversalType.MAP, targetClass = String.class, targetClass2 = String.class)
    private final HashMap<String, String> environmentMap;

    @Persist private CloseTabMode closeTabMode = CloseTabMode.NO_ERROR;

    public AWTProfile() {
        gfxInfo = new AWTGfxInfo();
        environmentMap = new HashMap<>();
        portForwardings = new ArrayList<>();
    }

    public AWTProfile(final AWTProfile copy) {
        gfxInfo = new AWTGfxInfo(copy.gfxInfo);
        environmentMap = new HashMap<>(copy.environmentMap);
        portForwardings = new ArrayList<>(copy.portForwardings);

        user = copy.user;
        host = copy.host;
        port = copy.port;
        timeout = copy.timeout;
        keepAliveCount = copy.keepAliveCount;
        keepAliveInterval = copy.keepAliveInterval;
        agentForwarding = copy.agentForwarding;
        xForwarding = copy.xForwarding;
        x11Host = copy.x11Host;
        x11Display = copy.x11Display;
        charsetName = copy.charsetName;
        charset = copy.charset;
        closeTabMode = copy.closeTabMode;
        command = copy.command;
        socksPort = copy.socksPort;
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

    @Override
    public List<Forwarding> getPortForwardings() {
        return portForwardings;
    }

    @Override
    public Integer getSocksPort() {
        return socksPort;
    }

    @Override
    public void setSocksPort(final Integer socksPort) {
        this.socksPort = socksPort;
    }

    @Override
    public void setKeepAliveInterval(final int interval) {
        this.keepAliveInterval = interval;
    }

    @Override
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    @Override
    public void setKeepAliveCount(final int count) {
        this.keepAliveCount = count;
    }

    @Override
    public int getKeepAliveCount() {
        return keepAliveCount;
    }
}
