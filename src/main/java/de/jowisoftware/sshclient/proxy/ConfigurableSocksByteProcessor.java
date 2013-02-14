package de.jowisoftware.sshclient.proxy;

public interface ConfigurableSocksByteProcessor {
    void setNextDispatcher(SocksDispatcher dispatcher);
    void finishSetup(String host, int port);
}
