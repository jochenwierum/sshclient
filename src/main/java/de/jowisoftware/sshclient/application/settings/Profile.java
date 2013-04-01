package de.jowisoftware.sshclient.application.settings;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import de.jowisoftware.sshclient.terminal.gfx.GfxInfo;

public interface Profile<C extends GfxInfo<?>> {
    String getDefaultTitle();

    String getUser();
    void setUser(final String user);
    String getHost();
    void setHost(final String host);
    void setPort(final int port);
    int getPort();
    void setTimeout(final int timeout);
    int getTimeout();
    void setKeepAliveInterval(int interval);
    int getKeepAliveInterval();
    void setKeepAliveCount(int count);
    int getKeepAliveCount();

    boolean getX11Forwarding();
    void setX11Forwarding(boolean forward);
    boolean getAgentForwarding();
    void setAgentForwarding(boolean forward);
    String getX11Host();
    int getX11Display();
    void setX11Host(String x11Host);
    void setX11Display(int x11Display);

    List<Forwarding> getPortForwardings();
    Integer getSocksPort();
    void setSocksPort(Integer socksPort);

    C getGfxSettings();

    Map<String, String> getEnvironment();

    void setCharsetName(String charsetName);
    @SuppressWarnings("UnusedDeclaration")
    String getCharsetName();
    Charset getCharset();

    boolean hasCommand();
    String getCommand();
    void setCommand(String command);
}
