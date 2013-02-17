package de.jowisoftware.sshclient.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

import java.net.UnknownHostException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class Version4InitialisationDispatcherTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    public Object[][] validConnectionEndPointsDataProvider() {
            return new Object[][] {
                { (byte) 0, (byte) 80, (byte) 66, (byte) 102, (byte) 7,
                        (byte) 99, 80 },
                { (byte) 2, (byte) 1, (byte) 127, (byte) 0, (byte) 0, (byte) 1,
                        513 }
            };
    }

    @Test
    @Parameters(method = "validConnectionEndPointsDataProvider")
    public void validDataSetsUpForwarding(final byte port1, final byte port2,
            final byte ip1, final byte ip2, final byte ip3, final byte ip4,
            final int port) throws UnknownHostException {
        final ConfigurableSocksByteProcessor processor = context
                .mock(ConfigurableSocksByteProcessor.class);

        final String ip = ip1 + "." + ip2 + "." + ip3 + "." + ip4;

        //@formatter:off
        context.checking(new Expectations() {{
            oneOf(processor).finishSetup(ip, port);
        }});
        //@formatter:on

        final Version4InitialisationDispatcher dispatcher = new Version4InitialisationDispatcher(
                processor);

        assertThat(dispatcher.process((byte) 1).length, is(0));
        assertThat(dispatcher.process(port1).length, is(0));
        assertThat(dispatcher.process(port2).length, is(0));
        assertThat(dispatcher.process(ip1).length, is(0));
        assertThat(dispatcher.process(ip2).length, is(0));
        assertThat(dispatcher.process(ip3).length, is(0));
        assertThat(dispatcher.process(ip4).length, is(0));
        assertArrayEquals(dispatcher.process((byte) 0),
                new byte[] { 0, 0x5a, port1, port2, ip1, ip2, ip3, ip4 });
    }

    public Object[][] illegalRequestDataProvider() {
        return new Object[][] {
                { 0 }, { 2 }, { 123 }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "illegalRequestDataProvider")
    public void listenRequestThrowsException(final int request) {
        new Version4InitialisationDispatcher(null).process((byte) request);
    }

    public Object[][] illegalUserFieldsDataProvider() {
        return new Object[][] {
                { 1 }, { 2 }, { 123 }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "illegalUserFieldsDataProvider")
    public void illegalUserRequestThrowsException(final int request) {
        final Version4InitialisationDispatcher dispatcher =
                new Version4InitialisationDispatcher(null);
        dispatcher.process((byte) 1);
        dispatcher.process((byte) 0);
        dispatcher.process((byte) 80);
        dispatcher.process((byte) 127);
        dispatcher.process((byte) 0);
        dispatcher.process((byte) 0);
        dispatcher.process((byte) 1);
        dispatcher.process((byte) request);
    }

    public Object[][] domainNameDataProvider() {
        return new Object[][] {
                { new byte[] { 1, 0, 22, 0, 0, 0, 1, 0, 'a', 'b', 'c', 0 },
                        "abc", 22 },
                { new byte[] { 1, 1, 1, 0, 0, 0, 4, 0, 'x', 0 }, "x", 257 }
        };
    }

    @Test
    @Parameters(method = "domainNameDataProvider")
    public void socks4aIsSupported(final byte[] input, final String host,
            final int port) {
        final ConfigurableSocksByteProcessor processor = context
                .mock(ConfigurableSocksByteProcessor.class);

        //@formatter:off
        context.checking(new Expectations() {{
            oneOf(processor).finishSetup(host, port);
        }});
        //@formatter:on

        final Version4InitialisationDispatcher dispatcher =
                new Version4InitialisationDispatcher(processor);

        for (int i = 0; i < input.length; ++i) {
            final byte[] result = dispatcher.process(input[i]);

            if (i == input.length - 1) {
                assertThat(result.length, is(8));
                assertThat(result[0], is((byte) 0));
                assertThat(result[1], is((byte) 0x5a));
                assertThat(result[2], is(input[1]));
                assertThat(result[3], is(input[2]));
                assertThat(result[4], is((byte) 127));
                assertThat(result[5], is((byte) 0));
                assertThat(result[6], is((byte) 0));
                assertThat(result[7], is((byte) 1));
            }
        }
    }
}
