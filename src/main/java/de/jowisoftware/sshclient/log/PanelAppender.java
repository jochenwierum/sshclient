package de.jowisoftware.sshclient.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class PanelAppender extends AppenderSkeleton {
    @Override
    protected void append(final LoggingEvent event) {
        final String message = this.layout.format(event);
        LogObserver.getInstance().triggerLog(
                new LogMessageContainer(message, event.getThrowableStrRep()));
    }

    @Override
    public void close() {
        /* ignored */
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}