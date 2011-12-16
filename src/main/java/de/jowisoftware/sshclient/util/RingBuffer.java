package de.jowisoftware.sshclient.util;

public interface RingBuffer<E> extends Iterable<E> {
    public E get(int i);
    public void append(E element);
    public int size();
    public E[] toArray(E[] array);
    public void clear();
}
