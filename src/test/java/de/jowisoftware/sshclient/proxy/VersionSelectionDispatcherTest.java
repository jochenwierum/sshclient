package de.jowisoftware.sshclient.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

import org.jmock.Expectations;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.JMockTest;

public class VersionSelectionDispatcherTest extends JMockTest {
    @Test public void
    version4SelectsVersion4InitialisationDispatcher() {
        final ConfigurableSocksByteProcessor processor = context
                .mock(ConfigurableSocksByteProcessor.class);

        final VersionSelectionDispatcher dispatcher = new VersionSelectionDispatcher(
                processor);

        //@formatter:off
        context.checking(new Expectations(){{
            oneOf(processor).setNextDispatcher(with(isA(Version4InitialisationDispatcher.class)));
        }});
        //@formatter:on

        assertThat(dispatcher.process((byte) 4).length, is(0));
    }

    @Test public void
    version5SelectsVersion4InitialisationDispatcher() {
        final ConfigurableSocksByteProcessor processor = context
                .mock(ConfigurableSocksByteProcessor.class);

        final VersionSelectionDispatcher dispatcher = new VersionSelectionDispatcher(
                processor);

        //@formatter:off
        context.checking(new Expectations(){{
            oneOf(processor).setNextDispatcher(with(isA(Version5InitialisationDispatcher.class)));
        }});
        //@formatter:on

        assertThat(dispatcher.process((byte) 5).length, is(0));
    }

    @DataProvider
    public Object[][] illegalVersionDataProvider() {
        return new Object[][] {
                { 9 }, { 3 }, { 6 }
        };
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "illegalVersionDataProvider")
    public void illegalVersionThrowsException(final int version) {
        final ConfigurableSocksByteProcessor processor = context
                .mock(ConfigurableSocksByteProcessor.class);

        final VersionSelectionDispatcher dispatcher = new VersionSelectionDispatcher(
                processor);

        dispatcher.process((byte) version);
    }
}
