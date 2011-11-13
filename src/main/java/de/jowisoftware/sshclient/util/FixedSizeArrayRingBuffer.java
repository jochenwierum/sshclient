package de.jowisoftware.sshclient.util;

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
    public E get(final int i) {
        return data[(start + i) % data.length];
    }

    @Override
    public void append(final E element) {
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
    public int size() {
        return full ? data.length : next;
    }
}
