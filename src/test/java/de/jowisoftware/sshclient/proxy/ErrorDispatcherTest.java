package de.jowisoftware.sshclient.proxy;

import org.testng.annotations.Test;

public class ErrorDispatcherTest {
    @Test(expectedExceptions = IllegalStateException.class)
    public void processThrowsError() {
        new ErrorDispatcher().process((byte) 0);
    }
}
