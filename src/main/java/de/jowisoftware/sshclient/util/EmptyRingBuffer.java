package de.jowisoftware.sshclient.util;

import java.util.Iterator;

public class EmptyRingBuffer<E> implements RingBuffer<E> {
    private static final Iterator<Object> EMPTY_ITERATOR = new Iterator<Object>() {
        @Override public boolean hasNext() { return false; }
        @Override public Object next() { return null; }
        @Override public void remove() { }
    };

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<E> iterator() {
        return (Iterator<E>) EMPTY_ITERATOR;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<E> reversed() {
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return (Iterator<E>) EMPTY_ITERATOR;
            }
        };
    }


    @SuppressWarnings("unchecked")
    @Override
    public E[] toArray(final E[] array) {
        return (E[]) new Object[0];
    }

    @Override public E get(final int i) { return null; }
    @Override public void append(final E element) { }
    @Override public int size() { return 0; }
    @Override public void clear() { }

    @Override
    public RingBuffer<E> getSnapshot() { return this; }
}
