package de.jowisoftware.sshclient.events;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class LinkedListEventHubTest {
    public static interface EventHubTestEvent {
        public void test1();
        public void test2(String param);
    }

    public static class EventProbe implements EventHubTestEvent {
        private boolean test1Called;
        private String test2Value;

        @Override
        public void test1() {
            test1Called = true;
        }

        @Override
        public void test2(final String param) {
            test2Value = param;

        }

        public boolean wasTest1Called() {
            return test1Called;
        }

        public String getTest2Value() {
            return test2Value;
        }
    }

    @Test
    public void registerListenerAndFireEvent() {
        final EventProbe probe = new EventProbe();
        final EventHub<EventHubTestEvent> hub =
                ReflectionEventHub.forEventClass(EventHubTestEvent.class);

        hub.register(probe);
        hub.fire().test1();
        hub.fire().test2("param");

        assertThat(probe.wasTest1Called(), is(true));
        assertThat(probe.getTest2Value(), is("param"));
    }

    @Test
    public void registerTwoListenerAndFireEvent() {
        final EventProbe probe1 = new EventProbe();
        final EventProbe probe2 = new EventProbe();
        final EventHub<EventHubTestEvent> hub =
                ReflectionEventHub.forEventClass(EventHubTestEvent.class);

        hub.register(probe1);
        hub.register(probe2);
        hub.fire().test1();

        assertThat(probe1.wasTest1Called(), is(true));
        assertThat(probe2.wasTest1Called(), is(true));
    }
}
