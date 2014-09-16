package de.jowisoftware.sshclient.proxy;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertArrayEquals;

@RunWith(JUnitParamsRunner.class)
public class Version5InitialisationDispatcherTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    public Object[][] validAuthentificationsDataProvider() {
        return new Object[][] {
                {new byte[]{0}},
                {new byte[]{0, 1}},
                {new byte[]{1, 3, 0}}
        };
    }

    @Test
    @Parameters(method = "validAuthentificationsDataProvider")
    public void validAuthentificationsAreAccepted(final byte[] methods) {
        final ConfigurableSocksByteProcessor processor = context.mock(ConfigurableSocksByteProcessor.class);

        // @formatter:off
        context.checking(new Expectations() {{
            oneOf(processor).setNextDispatcher(with(isA(
                    Version5InitialisationTargetDispatcher.class)));
        }});
        // @formatter:on

        final Version5InitialisationDispatcher dispatcher = new Version5InitialisationDispatcher(
                processor);

        dispatcher.process((byte) methods.length);
        for (int i = 0; i < methods.length; ++i) {
            final byte method = methods[i];

            if (i == methods.length - 1) {
                assertArrayEquals(dispatcher.process(method), new byte[] { 5,
                        0 });
            } else {
                assertArrayEquals(dispatcher.process(method), new byte[0]);
            }
        }
    }

    public Object[][] invalidAuthentificationsDataProvider() {
        return new Object[][] {
                { new byte[] { 1, 2, 3 } },
                { new byte[0] },
                { new byte[] { 2, 5 } },
                { new byte[] { (byte) 255 } }
        };
    }

    @Test(expected = RuntimeException.class)
    @Parameters(method = "invalidAuthentificationsDataProvider")
    public void invalidAuthentificationsAreAccepted(final byte[] methods) {
        final Version5InitialisationDispatcher dispatcher = new Version5InitialisationDispatcher(
                null);

        dispatcher.process((byte) methods.length);
        for (final byte method : methods) {
            dispatcher.process(method);
        }
    }
}
