package net.rtxyd.fallen.lib.util;

import java.util.HashMap;
import java.util.Map;

public final class ObjectCaky {

    private final Map<String, CakyEntry<?>> entries = new HashMap<>();

    @SuppressWarnings({"unchecked", "Synchronization", "SynchronizationOnLocalVariableOrMethodParameter"})
    public <S, T> T resolve(S obj, String key, CakyLoader<S, T> loader, CakyReviewer<S> reviewer) {
        int newFingerprint = reviewer.review(obj);
        CakyEntry<T> entry = (CakyEntry<T>) entries.get(key);

        if (entry == null) {
            synchronized (this) {
                entry = (CakyEntry<T>) entries.get(key);
                if (entry == null) {
                    T value = loader.load(obj);
                    entry = new CakyEntry<>(value, newFingerprint);
                    entries.put(key, entry);
                }
            }
        } else if (entry.fingerprint != newFingerprint) {
            synchronized (entry) {
                if (entry.fingerprint != newFingerprint) {
                    entry.value = loader.load(obj);
                    entry.fingerprint = newFingerprint;
                }
            }
        }
        return entry.value;
    }

    static final class CakyEntry<T> {
        volatile T value;
        volatile int fingerprint;

        CakyEntry(T value, int fingerprint) {
            this.value = value;
            this.fingerprint = fingerprint;
        }
    }

    @FunctionalInterface
    public static interface CakyLoader<S, T> {
        T load(S obj);
    }

    @FunctionalInterface
    public static interface CakyReviewer<S> {
        int review(S obj);
    }
}