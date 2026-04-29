package net.rtxyd.fallen.lib.util;

public interface IObjectCaky {

    @SuppressWarnings({"unchecked", "Synchronization", "SynchronizationOnLocalVariableOrMethodParameter"})
    <S, T> T resolve(S obj, String key, FinalObjectCaky.CakyLoader<S, T> loader, FinalObjectCaky.CakyReviewer<S> reviewer);

    static interface ICakyEntry<S, T> {

    }

    @FunctionalInterface
    public static interface CakyLoader<S, T> {
        T load(S obj);
    }

    @FunctionalInterface
    public static interface CakyReviewer<S> {
        int review(S obj);
    }

    enum Type {
        FINAL,
        MANUAL
    }
}
