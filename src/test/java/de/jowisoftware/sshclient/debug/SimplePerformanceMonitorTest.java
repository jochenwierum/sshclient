package de.jowisoftware.sshclient.debug;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.JMockTest;

public class SimplePerformanceMonitorTest extends JMockTest {
    private TimeSource timesource;
    private SimplePerformanceMonitor monitor;

    @BeforeMethod
    public void setupMocks() {
        timesource = context.mock(TimeSource.class);
        monitor = new SimplePerformanceMonitor(timesource);
    }

    private void expectDate(final long time) {
        context.checking(new Expectations() {{
            oneOf(timesource).getTime(); will(returnValue(time));
        }});
    }

    private void assertEndOfEventAt(final PerformanceType performanceType,
            final int expected) {
        final long time = monitor.end(performanceType);
        assertThat(Long.valueOf(time), is(equalTo(Long.valueOf(expected))));
    }

    @DataProvider(name = "taskData")
    public Object[][] taskData() {
        return new Object[][] {
                {200, 400, 200},
                {0, 500, 500}
        };
    }

    @Test(dataProvider = "taskData")
    public void measureOneTask(final int start, final int end,
            final int diff) {
        expectDate(start);
        monitor.start(PerformanceType.REQUEST_TO_RENDER);

        expectDate(end);
        assertEndOfEventAt(PerformanceType.REQUEST_TO_RENDER, diff);
    }

    @Test
    public void measerTwoTasks() {
        expectDate(100);
        monitor.start(PerformanceType.REQUEST_TO_RENDER);

        expectDate(200);
        monitor.start(PerformanceType.REVEICE_CHAR_TO_RENDER);

        expectDate(700);
        assertEndOfEventAt(PerformanceType.REQUEST_TO_RENDER, 600);

        expectDate(700);
        assertEndOfEventAt(PerformanceType.REVEICE_CHAR_TO_RENDER, 500);
    }

    @Test
    public void notStartedTasksYieldErrorCode() {
        expectDate(0);
        monitor.start(PerformanceType.REQUEST_TO_RENDER);
        expectDate(10);
        monitor.end(PerformanceType.REQUEST_TO_RENDER);

        expectDate(100);
        assertEndOfEventAt(PerformanceType.REQUEST_TO_RENDER,
                SimplePerformanceMonitor.NEVER_STARTED);
    }

    @Test
    public void firstEventWinsIfMarkedInEnum() {
        expectDate(100);
        monitor.start(PerformanceType.REQUEST_TO_RENDER);
        monitor.start(PerformanceType.REQUEST_TO_RENDER);

        expectDate(700);
        assertEndOfEventAt(PerformanceType.REQUEST_TO_RENDER, 600);
    }

    @Test
    public void lastEventWinsIfMarkedInEnum() {
        expectDate(100);
        monitor.start(PerformanceType.BACKGROUND_RENDER);

        expectDate(300);
        monitor.start(PerformanceType.BACKGROUND_RENDER);

        expectDate(700);
        assertEndOfEventAt(PerformanceType.BACKGROUND_RENDER, 400);
    }
}
