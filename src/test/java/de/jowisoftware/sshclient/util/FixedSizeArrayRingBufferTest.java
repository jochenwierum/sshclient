package de.jowisoftware.sshclient.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;

public class FixedSizeArrayRingBufferTest {
    public RingBuffer<Integer> buffer;

    @Before
    public void setUp() {
        buffer = new FixedSizeArrayRingBuffer<Integer>(10);
    }

    private void fillBuffer(final int ... with) {
        for (final int i : with) {
            buffer.append(i);
        }
    }

    @Test
    public void getAddedEntriesInNonFullList() {
        buffer.append(1);
        buffer.append(23);
        assertThat(buffer.size(), equalTo(2));
        buffer.append(38);
        buffer.append(49);
        assertThat(buffer.size(), equalTo(4));

        assertThat(buffer.get(3), equalTo(49));
        assertThat(buffer.get(0), equalTo(1));
        assertThat(buffer.get(1), equalTo(23));
        assertThat(buffer.get(2), equalTo(38));
    }

    @Test
    public void getAddedEntriesInFullList() {
        fillBuffer(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

        assertThat(buffer.size(), equalTo(10));
        assertThat(buffer.get(0), equalTo(3));
        assertThat(buffer.get(9), equalTo(12));
    }

    @Test
    public void iteratorContains10Newest() {
        fillBuffer(10, 2, 30, 4, 50, 60, 7, 80, 9, 100, 11, 120, 13, 140);
        assertThat(buffer, contains(50, 60, 7, 80, 9, 100, 11, 120, 13, 140));
    }

    @Test
    public void iteratorContains3Newest() {
        buffer = new FixedSizeArrayRingBuffer<Integer>(3);
        fillBuffer(10, 2, 30, 4, 50);
        assertThat(buffer, contains(30, 4, 50));
    }

    @Test
    public void reversedIteratorContains10Newest() {
        fillBuffer(10, 2, 30, 4, 50, 60, 7, 80, 9, 100, 11, 120, 13, 140);
        assertThat(buffer.reversed(), contains(140, 13, 120, 11, 100, 9, 80, 7, 60, 50));
    }

    @Test
    public void arrayConversionWithLessEntries() {
        fillBuffer(1, 2, 3);
        assertThat(buffer.toArray(new Integer[0]),
                equalTo(new Integer[]{1, 2, 3}));
    }

    @Test
    public void arrayConversionWithFullEntries() {
        fillBuffer(10, 2, 30, 4, 50, 60, 7, 80, 9, 100, 11, 120, 13, 140);
        assertThat(buffer.toArray(new Integer[0]),
                equalTo(new Integer[]{50, 60, 7, 80, 9, 100, 11, 120, 13, 140}));
    }

    @Test
    public void emptyArrayConversionReturnsEmptyArray() {
        assertThat(buffer.toArray(new Integer[0]),
                equalTo(new Integer[0]));
    }
}
