package de.jowisoftware.sshclient.proxy;


public class DefaultSocksInitialisationProcessor implements
        ConfigurableSocksByteProcessor,
        SocksDispatcher {
    private SocksDispatcher currentDispatcher;

    private boolean finishedSetup;
    private String host;
    private int port;

    DefaultSocksInitialisationProcessor(final SocksDispatcher defaultDispatcher) {
        if (defaultDispatcher == null) {
            this.currentDispatcher = new VersionSelectionDispatcher(this);
        } else {
            this.currentDispatcher = defaultDispatcher;
        }
    }

    DefaultSocksInitialisationProcessor() {
        this(null);
    }

    @Override
    public void setNextDispatcher(final SocksDispatcher dispatcher) {
        this.currentDispatcher = dispatcher;
    }

    @Override
    public byte[] process(final byte c) {
        return currentDispatcher.process(c);
    }

    @Override
    public void finishSetup(final String host, final int port) {
        finishedSetup = true;
        this.host = host;
        this.port = port;
        currentDispatcher = new ErrorDispatcher();
    }

    public boolean isFinished() {
        return finishedSetup;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
