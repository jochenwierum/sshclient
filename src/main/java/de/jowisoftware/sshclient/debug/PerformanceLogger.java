package de.jowisoftware.sshclient.debug;

import de.jowisoftware.sshclient.util.FixedSizeArrayRingBuffer;
import de.jowisoftware.sshclient.util.RingBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PerformanceLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceLogger.class);

    private static final int LIST_SIZE = 100;

    public static final PerformanceLogger INSTANCE = new PerformanceLogger();

    private final SimplePerformanceMonitor monitor = new SimplePerformanceMonitor();
    private final Map<PerformanceType, RingBuffer<Long>> recentTimings = new HashMap<>();

    private PerformanceWindow window;

    public static void start(final PerformanceType performanceType) {
        INSTANCE.startLogging(performanceType);
    }

    public static void end(final PerformanceType performanceType) {
        INSTANCE.endLogging(performanceType);
    }

    public void showWindowProfiling() {
        getWindow().setVisible(true);
    }

    private PerformanceWindow getWindow() {
        if (window == null) {
            window = new PerformanceWindow(recentTimings);
        }
        return window;
    }

    private PerformanceLogger() {
        prepareTimingList();
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
                LOGGER.trace("Timing of {}: {} ms", performanceType.niceName, time);
            }
        }
    }

    public void quit() {
        getWindow().dispose();
    }
}
