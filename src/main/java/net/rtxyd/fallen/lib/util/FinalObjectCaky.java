package net.rtxyd.fallen.lib.util;

import java.util.HashMap;
import java.util.Map;

public final class FinalObjectCaky implements IObjectCaky {

    private final Map<String, FinalCakyEntryD<?, ?>> entries = new HashMap<>();

    @SuppressWarnings({"unchecked", "Synchronization", "SynchronizationOnLocalVariableOrMethodParameter"})
    @Override
    public <S, T> T resolve(S obj, String key, CakyLoader<S, T> loader, CakyReviewer<S> reviewer) {
        FinalCakyEntryD<S, T> entry = (FinalCakyEntryD<S, T>) entries.get(key);

        if (entry == null) {
            synchronized (this) {
                entry = (FinalCakyEntryD<S, T>) entries.get(key);
                if (entry == null) {
                    T value = loader.load(obj);
                    entry = new FinalCakyEntryD<>(value, reviewer.review(obj), loader, reviewer);
                    entries.put(key, entry);
                }
            }
        } else {
            int newFingerprint = entry.reviewer.review(obj);
            if (entry.fingerprint != newFingerprint) {
                synchronized (entry) {
                    if (entry.fingerprint != newFingerprint) {
                        entry.value = entry.loader.load(obj);
                        entry.fingerprint = entry.reviewer.review(obj);
                    }
                }
            }
        }
        return entry.value;
    }

    static final class FinalCakyEntryD<S, T> implements ICakyEntry<S, T> {
        volatile T value;
        volatile int fingerprint;
        CakyLoader<S, T> loader;
        CakyReviewer<S> reviewer;

        FinalCakyEntryD(T value, int fingerprint, CakyLoader<S, T> loader, CakyReviewer<S> reviewer) {
            this.value = value;
            this.fingerprint = fingerprint;
            this.loader = loader;
            this.reviewer = reviewer;
        }
    }
}