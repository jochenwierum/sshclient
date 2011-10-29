package de.jowisoftware.sshclient.terminal;

import java.nio.charset.Charset;
import java.util.Map;

public interface Profile<C> {
    String getDefaultTitle();
    String getUser();
    void setUser(final String user);
    String getHost();
    void setHost(final String host);
    void setPort(final int port);
    int getPort();
    void setTimeout(final int timeout);
    int getTimeout();
    Charset getCharset();
    void setCharset(final Charset charset);
    GfxInfo<C> getGfxSettings();
    Map<String, String> getEnvironment();
}