package de.jowisoftware.sshclient.debug;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class SimplePerformanceMonitorTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

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
            final long expected) {
        final long time = monitor.end(performanceType);
        assertThat(time, is(equalTo(expected)));
    }

    @SuppressWarnings("UnusedDeclaration")
    public int[][] taskData() {
        return new int[][] {
                {200, 400, 200},
                {0, 500, 500}
        };
    }

    @Test
    @Parameters(method = "taskData")
    public void measureOneTask(final int data[]) {
        expectDate(data[0]);
        monitor.start(PerformanceType.REQUEST_TO_RENDER);

        expectDate(data[1]);
        assertEndOfEventAt(PerformanceType.REQUEST_TO_RENDER, data[2]);
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
