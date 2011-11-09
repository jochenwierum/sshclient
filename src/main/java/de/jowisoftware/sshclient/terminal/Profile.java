package de.jowisoftware.sshclient.terminal;

import java.nio.charset.Charset;
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
    C getGfxSettings();
    Map<String, String> getEnvironment();
    void setCharsetName(String charsetName);
    String getCharsetName();
    Charset getCharset();
}