package net.rtxyd.fallen.lib.runtime.forgemod.util;

import org.jetbrains.annotations.Nullable;

public interface IHolder<C, T> {
    C getClassifier();
    T get();
    static <C, T> IHolder<C, T> create(C c, T t) {
        return new SimpleHolder<>(c, t);
    }
    class SimpleHolder<C, T> implements IHolder<C, T> {
        private final C classifier;
        private final @Nullable T value;

        private SimpleHolder(C classifier, @Nullable T value) {
            this.classifier = classifier;
            this.value = value;
        }

        @Override
        public C getClassifier() {
            return classifier;
        }

        @Override
        public T get() {
            return value;
        }
    }
}
