package de.jowisoftware.sshclient.debug;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SimplePerformanceMonitorTest {
    private final Mockery context = new JUnit4Mockery();
    private TimeSource timesource;
    private SimplePerformanceMonitor monitor;

    @Before
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

    @Test
    public void measureOneTaskWith200ms() {
        expectDate(200);
        monitor.start(PerformanceType.REQUEST_TO_RENDER);

        expectDate(400);
        assertEndOfEventAt(PerformanceType.REQUEST_TO_RENDER, 200);
    }

    @Test
    public void measureOneTaskWith500ms() {
        expectDate(0);
        monitor.start(PerformanceType.REQUEST_TO_RENDER);

        expectDate(500);
        assertEndOfEventAt(PerformanceType.REQUEST_TO_RENDER, 500);
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
        monitor.start(PerformanceType.SELECT_TO_RENDER);

        expectDate(300);
        monitor.start(PerformanceType.SELECT_TO_RENDER);

        expectDate(700);
        assertEndOfEventAt(PerformanceType.SELECT_TO_RENDER, 400);
    }
}
