package de.jowisoftware.sshclient.util;

import java.util.Arrays;
import java.util.Iterator;

public class FixedSizeArrayRingBuffer<E> implements RingBuffer<E> {
    private final E[] data;
    private boolean full = false;
    private int next = 0;
    private int start = 0;

    @SuppressWarnings("unchecked")
    public FixedSizeArrayRingBuffer(final int size) {
        data = (E[]) new Object[size];
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int iteratorPosition = 0;

            @Override
            public boolean hasNext() {
                return iteratorPosition < size();
            }

            @Override
            public E next() {
                return get(iteratorPosition++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public synchronized E get(final int i) {
        return data[(start + i) % data.length];
    }

    @Override
    public synchronized void append(final E element) {
        if (next == start && full) {
            start = increaseModulo(start);
        }

        data[next] = element;
        next = increaseModulo(next);

        if (next == 0) {
            full = true;
        }
    }

    private int increaseModulo(final int x) {
        return (x + 1) % data.length;
    }

    @Override
    public synchronized int size() {
        return full ? data.length : next;
    }

    @Override
    public synchronized E[] toArray(final E[] array) {
        final E[] result = Arrays.copyOf(array, size());
        if (start < next && result.length > 0) {
            System.arraycopy(data, start, result, 0, next);
        } else if(result.length > 0) {
            System.arraycopy(data, start, result, 0, data.length - start);
            System.arraycopy(data, 0, result, data.length - start, next);
        }
        return result;
    }

    @Override
    public void clear() {
        start = 0;
        next = 0;
        full = false;
    }
}
