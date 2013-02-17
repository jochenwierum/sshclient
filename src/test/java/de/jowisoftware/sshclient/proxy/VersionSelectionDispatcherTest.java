package de.jowisoftware.sshclient.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class VersionSelectionDispatcherTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

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

    public Object[][] illegalVersionDataProvider() {
        return new Object[][] {
                { 9 }, { 3 }, { 6 }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "illegalVersionDataProvider")
    public void illegalVersionThrowsException(final int version) {
        final ConfigurableSocksByteProcessor processor = context
                .mock(ConfigurableSocksByteProcessor.class);

        final VersionSelectionDispatcher dispatcher = new VersionSelectionDispatcher(
                processor);

        dispatcher.process((byte) version);
    }
}
