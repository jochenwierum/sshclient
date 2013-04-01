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

    private FixedSizeArrayRingBuffer(final FixedSizeArrayRingBuffer<E> other) {
        this.full = other.full;
        this.start = other.start;
        this.next = other.next;
        this.data = other.data.clone();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int iteratorPosition = 0;

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
    public Iterable<E> reversed() {
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return new Iterator<E>() {
                    private int iteratorPosition = size();

                    @Override
                    public boolean hasNext() {
                        return iteratorPosition > 0;
                    }

                    @Override
                    public E next() {
                        return get(--iteratorPosition);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public RingBuffer<E> getSnapshot() {
        return new FixedSizeArrayRingBuffer<>(this);
    }
}
