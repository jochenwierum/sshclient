package de.jowisoftware.sshclient.debug;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class QuantileTest {
    private final Quantile q1 = new Quantile(new long[]{1, 10, 2, 9, 3, 8, 4, 7, 5, 6});
    private final Quantile q2 = new Quantile(new long[]{200, 201, 202, 210, 203,
            204, 205, 206, 207, 208, 209});

    @Test
    public void median() {
        assertThat(q1.getMedian(), is(equalTo(6l)));
        assertThat(q2.getMedian(), is(equalTo(205l)));
    }

    @Test
    public void quantiles() {
        assertThat(q1.getQuantile(0.5), is(equalTo(5.5)));
        assertThat(q2.getQuantile(0.5), is(equalTo(205.0)));

        assertThat(q1.getQuantile(0.25), is(equalTo(3.0)));
        assertThat(q2.getQuantile(0.25), is(equalTo(202.0)));
    }

    @Test
    public void bottomAntennas() {
        final Quantile q = new Quantile(new long[] {
                9, 6, 7, 7, 3, 9, 10, 1, 8, 7, 9, 9,
                    8, 10, 5, 10, 10, 9, 10, 8});

        final long[] antennas = q.getBottomAntennas();
        assertThat(antennas.length, is(equalTo(2)));
        assertThat(antennas[0], is(equalTo(1l)));
        assertThat(antennas[1], is(equalTo(3l)));

        assertThat(q.getMinValueWithoutAntenna(), is(equalTo(5l)));
    }

    @Test
    public void testEmptyAntennas() {
        final Quantile q = new Quantile(new long[] {1, 2, 3, 4, 5});

        assertThat(q.getTopAntennas(), is(equalTo(new long[0])));
        assertThat(q.getBottomAntennas(), is(equalTo(new long[0])));

        assertThat(q.getMinValueWithoutAntenna(), is(equalTo(1l)));
        assertThat(q.getMaxValueWithoutAntenna(), is(equalTo(5l)));
    }

    @Test
    public void topAntennas() {
        final Quantile q = new Quantile(new long[] {
                2, 5, 4, 4, 8, 2, 1, 10, 3, 4, 2, 2,
                    3, 1, 6, 1, 1, 2, 1, 3});

        final long[] antennas = q.getTopAntennas();
        assertThat(antennas.length, is(equalTo(2)));
        assertThat(antennas, is(equalTo(new long[]{8, 10})));

        assertThat(q.getMaxValueWithoutAntenna(), is(equalTo(6l)));
    }
}
