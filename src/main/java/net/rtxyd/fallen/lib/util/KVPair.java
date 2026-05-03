package net.rtxyd.fallen.lib.util;

import org.jetbrains.annotations.Nullable;

public class KVPair<S, T> {
    private S key;
    private T value;

    public KVPair(S key, @Nullable T value) {
        this.key = key;
        this.value = value;
    }

    public S getKey() {
        return key;
    }
    public T getValue() {
        return value;
    }
    public S setKey(S key) {
        this.key = key;
        return this.key;
    }
    public T setValue(T value) {
        this.value = value;
        return this.value;
    }
}
