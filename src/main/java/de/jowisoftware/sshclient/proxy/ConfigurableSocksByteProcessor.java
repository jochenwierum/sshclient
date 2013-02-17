package de.jowisoftware.sshclient.proxy;

interface ConfigurableSocksByteProcessor {
    void setNextDispatcher(SocksDispatcher dispatcher);
    void finishSetup(String host, int port);
}
