package de.jowisoftware.sshclient.debug;

import java.util.HashMap;
import java.util.Map;

public class SimplePerformanceMonitor  {
    public static final int NEVER_STARTED = -1;

    private final TimeSource timeSource;
    private final Map<PerformanceType, Long> startTimes =
            new HashMap<PerformanceType, Long>();

    public SimplePerformanceMonitor() {
        this(new SystemTimeSource());
    }

    public SimplePerformanceMonitor(final TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    public void start(final PerformanceType performanceType) {
        if (!startTimes.containsKey(performanceType) ||
                !performanceType.firstEventIsMoreImportant) {
            startTimes.put(performanceType, timeSource.getTime());
        }
    }

    public long end(final PerformanceType performanceType) {
        final long endTime = timeSource.getTime();

        if (startTimes.containsKey(performanceType)) {
            final long startTime = startTimes.get(performanceType);
            startTimes.remove(performanceType);
            return endTime - startTime;
        } else {
            return NEVER_STARTED;
        }
    }
}
