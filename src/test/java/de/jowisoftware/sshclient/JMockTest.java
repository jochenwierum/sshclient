package de.jowisoftware.sshclient;

import org.jmock.Mockery;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class JMockTest {
    protected Mockery context;

    @BeforeMethod
    public void setUpJMock() {
        context = new Mockery();
    }

    @AfterMethod
    public void checkJMock() {
        context.assertIsSatisfied();
    }
}
