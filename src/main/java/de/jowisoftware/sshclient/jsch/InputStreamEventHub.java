package de.jowisoftware.sshclient.jsch;

import java.util.LinkedList;
import java.util.List;

import de.jowisoftware.sshclient.events.EventHub;

/**
 * Reacts exaclty as an ReflectionEventHub, but is more faster because it does
 * not use reflections for invokes
 */
public class InputStreamEventHub implements EventHub<InputStreamEvent> {
    private final List<InputStreamEvent> handlers = new LinkedList<InputStreamEvent>();

    private final InputStreamEvent invoker = new InputStreamEvent() {
        @Override
        public void gotChars(final byte[] buffer, final int read) {
            for (final InputStreamEvent handler : handlers) {
                handler.gotChars(buffer, read);
            }
        }

        @Override
        public void streamClosed(final int exitCode) {
            for (final InputStreamEvent handler : handlers) {
                handler.streamClosed(exitCode);
            }
        }
    };

    @Override
    public void register(final InputStreamEvent listener) {
        handlers.add(listener);
    }

    @Override
    public InputStreamEvent fire() {
        return invoker;
    }
}
