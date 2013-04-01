package de.jowisoftware.sshclient.debug;

import java.util.Arrays;

public class Quantile {
    private final long[] values;

    public Quantile(final Long[] values) {
        this(convertLongArray(values));
    }

    private static long[] convertLongArray(final Long[] array) {
        final long[] result = new long[array.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }

    public Quantile(final long[] values) {
        this.values = values.clone();
        Arrays.sort(this.values);
    }

    public long getMedian() {
        final int index = values.length / 2;
        return values[index];
    }

    public double getQuantile(final double p) {
        final double floatingIndex = p * values.length;
        final int index = (int) floatingIndex;

        if (floatingIndex - index == 0) {
            return (values[index] + values[index - 1]) / 2.0;
        } else {
            return values[index];
        }
    }

    public long getMinValueWithoutAntenna() {
        return values[getMinIndex()];
    }

    public long getMaxValueWithoutAntenna() {
        return values[getMaxIndex()];
    }

    public long getMaxValue() {
        return values[values.length - 1];
    }

    public long[] getBottomAntennas() {
        final int count = getMinIndex();
        return copyArray(0, count);
    }

    public long[] getTopAntennas() {
        final int start = getMaxIndex();
        return copyArray(start + 1, values.length);
    }

    private int getMinIndex() {
        final double q25 = getQuantile(.25);
        final double iqr = getQuantile(.75) - q25;
        final double minValue = q25 - 1.5 * iqr;
        return findSmallestIndexBiggerThan(minValue);
    }

    private int findSmallestIndexBiggerThan(final double minValue) {
        int count = 0;
        while (values[count] < minValue) {
            ++count;
        }
        return count;
    }

    private int getMaxIndex() {
        final double q75 = getQuantile(.75);
        final double iqr = q75 - getQuantile(.25);
        final double maxValue = q75 + 1.5 * iqr;
        return findBiggestIndexSmallerThan(maxValue);
    }

    private int findBiggestIndexSmallerThan(final double maxValue) {
        int start = values.length - 1;
        while (values[start] > maxValue) {
            --start;
        }
        return start;
    }


    private long[] copyArray(final int start, final int end) {
        final long[] result = new long[end - start];
        System.arraycopy(values, start, result, 0, end - start);
        return result;
    }
}
