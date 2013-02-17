package de.jowisoftware.sshclient.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.UnsupportedEncodingException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class Version5InitialisationTargetDispatcherTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    public Object[][] illegalVersionsDataProvider() {
        return new Object[][] {
                { 1 },
                { 4 },
                { 6 },
                { 256 }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "illegalVersionsDataProvider")
    public void illegalVersionIsRejected(final int version) {
        new Version5InitialisationTargetDispatcher(null)
                .process((byte) version);
    }

    public Object[][] illegalCommandsDataProvider() {
        return new Object[][] {
                { 2 },
                { 3 },
                { 8 }
        };
    }

    @Test
    @Parameters(method = "illegalCommandsDataProvider")
    public void illegalCommandsAreRejected(final int direction) {
        final Version5InitialisationTargetDispatcher dispatcher =
                new Version5InitialisationTargetDispatcher(null);

        assertThat(dispatcher.process((byte) 5).length, is(0));

        try {
            dispatcher.process((byte) direction);
            Assert.fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void reservedByteIsZero() {
        final Version5InitialisationTargetDispatcher dispatcher =
                new Version5InitialisationTargetDispatcher(null);
        dispatcher.process((byte) 5);
        dispatcher.process((byte) 1);
        assertThat(dispatcher.process((byte) 0).length, is(0));
    }

    public Object[][] invalidResevedByteValues() {
        return new Object[][] {
                { 1 },
                { 2 },
                { 42 }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidResevedByteValues")
    public void reservedBytesAreRejected(final int reserved) {
        final Version5InitialisationTargetDispatcher dispatcher = new Version5InitialisationTargetDispatcher(
                null);
        dispatcher.process((byte) 5);
        dispatcher.process((byte) 1);
        dispatcher.process((byte) reserved);
    }

    public Object[][] validATypeDataProviders() {
        return new Object[][] {
                { 1 },
                { 3 },
                { 4 }
        };
    }

    public Object[][] invalidATypeDataProviders() {
        return new Object[][] {
                { 0 },
                { 2 },
                { 5 },
                { 23 }
        };
    }

    @Test
    @Parameters(method = "validATypeDataProviders")
    public void validDestinationsAreNotRejected(final int atype) {
        callWithDestinationType(atype);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidATypeDataProviders")
    public void invalidDestinationTypesAreRejected(final int atype) {
        callWithDestinationType(atype);
    }

    private void callWithDestinationType(final int atype) {
        final Version5InitialisationTargetDispatcher dispatcher = new Version5InitialisationTargetDispatcher(
                null);
        dispatcher.process((byte) 5);
        dispatcher.process((byte) 1);
        dispatcher.process((byte) 0);
        assertThat(dispatcher.process((byte) atype).length, is(0));
    }

    public Object[][] successfullCallsDataProvider()
            throws UnsupportedEncodingException {
        return new Object[][] {
                { new byte[] { 1, 127, 0, 0, 1, 0, 22 },
                        "127.0.0.1", 22 },
                { new byte[] { 1, (byte) 192, (byte) 168, 1, 1, 1, 1 },
                        "192.168.1.1", 257 },
                { new byte[] { 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        1, 0, 80 }, "0:0:0:0:0:0:0:1", 80 },
                { new byte[] { 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        2, 0, 80 }, "101:101:101:101:101:101:101:102", 80 },
                { new byte[] { 3, 4, (byte) 'A', (byte) 'B', (byte) 'C',
                        (byte) 'D', 1, 1 }, "ABCD", 257 },
                { new byte[] { 3, 2, (byte) '1', (byte) '2', 0, 22 }, "12", 22 },
        };
    }

    @Test
    @Parameters(method = "successfullCallsDataProvider")
    public void successfullCallsAreForwarded(final byte[] input,
            final String address, final int port) {
        final ConfigurableSocksByteProcessor processor =
                context.mock(ConfigurableSocksByteProcessor.class);

        final Version5InitialisationTargetDispatcher dispatcher = new Version5InitialisationTargetDispatcher(
                processor);

        //@formatter:off
        context.checking(new Expectations() {{
            oneOf(processor).finishSetup(address, port);
        }});
        //@formatter:on

        assertThat(dispatcher.process((byte) 5).length, is(0));
        assertThat(dispatcher.process((byte) 1).length, is(0));
        assertThat(dispatcher.process((byte) 0).length, is(0));

        for (int i = 0; i < input.length; ++i) {
            final byte[] result = dispatcher.process(input[i]);

            if (i < input.length - 1) {
                assertThat(result.length, is(0));
            } else {
                assertThat("result length differs", result.length,
                        is(3 + input.length));
                assertThat("byte 0 differs", result[0], is((byte) 5));
                assertThat("byte 1 differs", result[1], is((byte) 0));
                assertThat("byte 2 differs", result[2], is((byte) 0));
                for (int j = 0; j < input.length; ++j) {
                    assertThat("byte " + (j + 3) + " differs", result[j + 3],
                            is(input[j]));
                }
            }

        }
    }
}
