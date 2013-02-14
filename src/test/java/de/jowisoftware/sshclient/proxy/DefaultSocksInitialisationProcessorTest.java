package de.jowisoftware.sshclient.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;

import org.jmock.Expectations;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.JMockTest;

public class DefaultSocksInitialisationProcessorTest extends JMockTest {
    @Test
    public void dispatcherIsSelectable() {
        final SocksDispatcher dispatcher1 = context.mock(SocksDispatcher.class,
                "dispatcher1");
        final SocksDispatcher dispatcher2 = context.mock(SocksDispatcher.class,
                "dispatcher2");

        final DefaultSocksInitialisationProcessor processor =
                new DefaultSocksInitialisationProcessor();

        // @formatter:off
        context.checking(new Expectations() {{
            oneOf(dispatcher1).process((byte) 65); will(returnValue(new byte[] {'1', '2'}));
            oneOf(dispatcher1).process((byte) 66); will(returnValue(new byte[0]));
            oneOf(dispatcher2).process((byte) 67); will(returnValue(new byte[] {'3', '4'}));
        }});
        // @formatter:on

        processor.setNextDispatcher(dispatcher1);
        final byte[] aResult = processor.process((byte) 65);
        final byte[] bResult = processor.process((byte) 66);

        processor.setNextDispatcher(dispatcher2);
        final byte[] cResult = processor.process((byte) 67);

        assertEquals(aResult, new byte[] { '1', '2' });
        assertEquals(bResult, new byte[0]);
        assertEquals(cResult, new byte[] { '3', '4' });
    }

    @Test
    public void setupConnectionChangesState() {
        final DefaultSocksInitialisationProcessor processor =
                new DefaultSocksInitialisationProcessor();

        assertThat(processor.isFinished(), is(false));

        processor.finishSetup("ip", 1234);
        assertThat(processor.isFinished(), is(true));
        assertThat(processor.getHost(), is("ip"));
        assertThat(processor.getPort(), is(1234));

        processor.finishSetup("ip2", 4321);
        assertThat(processor.isFinished(), is(true));
        assertThat(processor.getHost(), is("ip2"));
        assertThat(processor.getPort(), is(4321));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void setupConnectionFinishesState() {
        final DefaultSocksInitialisationProcessor processor =
                new DefaultSocksInitialisationProcessor();

        processor.finishSetup("ip", 1234);
        processor.process((byte) 0);
    }
}
