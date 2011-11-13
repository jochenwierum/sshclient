package de.jowisoftware.sshclient.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.util.FixedSizeArrayRingBuffer;
import de.jowisoftware.sshclient.util.RingBuffer;

public class PerformanceLogger {
    private static final Logger LOGGER = Logger
            .getLogger(PerformanceLogger.class);

    private static final long THREAD_SLEEP_TIME_IN_MS = 5000;
    private static final int LIST_SIZE = 100;

    private static PerformanceLogger instance = new PerformanceLogger();

    private final SimplePerformanceMonitor monitor = new SimplePerformanceMonitor();
    private final Map<PerformanceType, RingBuffer<Long>> recentTimings =
            new HashMap<PerformanceType, RingBuffer<Long>>();

    private final boolean enabled;

    public static void start(final PerformanceType performanceType) {
        instance.startLogging(performanceType);
    }

    public static void end(final PerformanceType performanceType) {
        instance.endLogging(performanceType);
    }

    private PerformanceLogger() {
        enabled = LOGGER.isDebugEnabled();

        if (enabled) {
            prepareTimingList();
            startThread();
        }
    }

    private void prepareTimingList() {
        for (final PerformanceType type : PerformanceType.values()) {
            recentTimings.put(type, new FixedSizeArrayRingBuffer<Long>(LIST_SIZE));
        }
    }

    private void startThread() {
        final Thread thread = new Thread("PerformanceLoggerThread") {
            @Override public void run() {
                while(enabled) {
                    try {
                        Thread.sleep(THREAD_SLEEP_TIME_IN_MS);
                        logStats();
                    } catch (final Exception e) {
                        LOGGER.error("Exception in Performance-Thread", e);
                    }
                }
            }
        };
        thread.setPriority(Thread.NORM_PRIORITY - 2);
        thread.setDaemon(true);
        thread.start();
    }

    private synchronized void startLogging(final PerformanceType performanceType) {
        monitor.start(performanceType);
    }

    private synchronized void endLogging(final PerformanceType performanceType) {
        if (!enabled) {
            return;
        }

        final long time = monitor.end(performanceType);

        if (time != SimplePerformanceMonitor.NEVER_STARTED) {
            recentTimings.get(performanceType).append(time);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Timing of " + performanceType.niceName +
                        ": " + time + " ms");
            }
        }
    }

    public synchronized void logStats() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Timings of last ").append(LIST_SIZE).append(" ops (min/max/avg):\n");

        for (final Entry<PerformanceType, RingBuffer<Long>> entry : recentTimings.entrySet()) {
            builder.append("\t").append(entry.getKey().niceName).append(": ");
            appendMinMaxAvg(entry.getValue(), builder);
        }

        LOGGER.debug(builder.toString());
    }

    private void appendMinMaxAvg(final RingBuffer<Long> buffer, final StringBuilder builder) {
        if (buffer.size() > 0) {
            long min = Integer.MAX_VALUE;
            long max = Integer.MIN_VALUE;
            long avg = 0;

            for (final long value : buffer) {
                min = Math.min(min, value);
                max = Math.max(max, value);
                avg += value;
            }

            avg /= buffer.size();

            builder.append(min).append(" ms / ");
            builder.append(max).append(" ms / ");
            builder.append(avg).append(" ms\n");
        } else {
            builder.append("No timings yet\n");
        }
    }
}
