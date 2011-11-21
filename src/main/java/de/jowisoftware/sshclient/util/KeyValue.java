package de.jowisoftware.sshclient.util;

public class KeyValue<T1, T2> {
    public final T1 key;
    public final T2 value;

    public KeyValue(final T1 key, final T2 value) {
        this.key = key;
        this.value = value;
    }
}
