package de.jowisoftware.sshclient.debug;

import org.apache.log4j.Logger;

public class PerformanceLogger {
    private static final Logger LOGGER = Logger
            .getLogger(PerformanceLogger.class);

    private static PerformanceLogger instance = new PerformanceLogger();
    private final SimplePerformanceMonitor monitor = new SimplePerformanceMonitor();
    private final boolean enabled;

    private PerformanceLogger() {
        enabled = LOGGER.isTraceEnabled();
    }

    public static void start(final PerformanceType performanceType) {
        instance.startLogging(performanceType);
    }

    public static void end(final PerformanceType performanceType) {
        instance.endLogging(performanceType);
    }

    private void startLogging(final PerformanceType performanceType) {
        monitor.start(performanceType);
    }

    private void endLogging(final PerformanceType performanceType) {
        if (!enabled) {
            return;
        }

        final long time = monitor.end(performanceType);
        if (time != SimplePerformanceMonitor.NEVER_STARTED) {
            LOGGER.trace("Timing of " + performanceType.niceName + ": " + time + " ms");
        }
    }
}
