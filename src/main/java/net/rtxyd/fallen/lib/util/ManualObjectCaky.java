package net.rtxyd.fallen.lib.util;

import java.util.HashMap;
import java.util.Map;

public class ManualObjectCaky implements IObjectCaky {

    private final Map<String, ManualCakyEntry<?, ?>> entries = new HashMap<>();

    @SuppressWarnings({"unchecked", "Synchronization", "SynchronizationOnLocalVariableOrMethodParameter"})
    @Override
    public <S, T> T resolve(S obj, String key, CakyLoader<S, T> loader, CakyReviewer<S> reviewer) {
        ManualCakyEntry<S, T> entry = (ManualCakyEntry<S, T>) entries.get(key);

        if (entry == null) {
            synchronized (this) {
                entry = (ManualCakyEntry<S, T>) entries.get(key);
                if (entry == null) {
                    T value = loader.load(obj);
                    entry = new ManualCakyEntry<>(value, reviewer.review(obj));
                    entries.put(key, entry);
                }
            }
        } else {
            int newFingerprint = reviewer.review(obj);
            if (entry.fingerprint != newFingerprint) {
                synchronized (entry) {
                    if (entry.fingerprint != newFingerprint) {
                        entry.value = loader.load(obj);
                        entry.fingerprint = reviewer.review(obj);
                    }
                }
            }
        }
        return entry.value;
    }

    static final class ManualCakyEntry<S, T> implements ICakyEntry<S, T> {
        volatile T value;
        volatile int fingerprint;

        ManualCakyEntry(T value, int fingerprint) {
            this.value = value;
            this.fingerprint = fingerprint;
        }
    }
}
