package de.jowisoftware.sshclient.terminal;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

public interface Profile<C extends GfxInfo<?>> extends Serializable {
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