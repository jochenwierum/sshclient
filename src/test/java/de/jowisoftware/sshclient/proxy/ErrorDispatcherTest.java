package de.jowisoftware.sshclient.proxy;

import org.junit.Test;

public class ErrorDispatcherTest {
    @Test(expected = IllegalStateException.class)
    public void processThrowsError() {
        new ErrorDispatcher().process((byte) 0);
    }
}
