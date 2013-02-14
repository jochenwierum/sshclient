package de.jowisoftware.sshclient.proxy;

public class ErrorDispatcher implements SocksDispatcher {

    @Override
    public byte[] process(final byte c) {
        error();
        return null;
    }

    private void error() {
        throw new IllegalStateException("The socket has already been setup");
    }
}
