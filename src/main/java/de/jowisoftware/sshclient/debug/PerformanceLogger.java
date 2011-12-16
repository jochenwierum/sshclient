package de.jowisoftware.sshclient.debug;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.util.FixedSizeArrayRingBuffer;
import de.jowisoftware.sshclient.util.RingBuffer;

public class PerformanceLogger {
    private static final Logger LOGGER = Logger
            .getLogger(PerformanceLogger.class);

    private static final int LIST_SIZE = 100;

    private static PerformanceLogger instance = new PerformanceLogger();

    private final SimplePerformanceMonitor monitor = new SimplePerformanceMonitor();
    private final Map<PerformanceType, RingBuffer<Long>> recentTimings =
            new HashMap<PerformanceType, RingBuffer<Long>>();

    private final PerformanceWindow window;

    public static void start(final PerformanceType performanceType) {
        instance.startLogging(performanceType);
    }

    public static void end(final PerformanceType performanceType) {
        instance.endLogging(performanceType);
    }

    public static void showWindowProfiling() {
        instance.window.setVisible(true);
    }

    private PerformanceLogger() {
        prepareTimingList();
        window = new PerformanceWindow(recentTimings);
    }

    private void prepareTimingList() {
        for (final PerformanceType type : PerformanceType.values()) {
            recentTimings.put(type, new FixedSizeArrayRingBuffer<Long>(LIST_SIZE));
        }
    }

    private synchronized void startLogging(final PerformanceType performanceType) {
        monitor.start(performanceType);
    }

    private synchronized void endLogging(final PerformanceType performanceType) {
        final long time = monitor.end(performanceType);

        if (time != SimplePerformanceMonitor.NEVER_STARTED) {
            recentTimings.get(performanceType).append(time);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Timing of " + performanceType.niceName +
                        ": " + time + " ms");
            }
        }
    }

    public static void quit() {
        instance.window.dispose();
    }
}
