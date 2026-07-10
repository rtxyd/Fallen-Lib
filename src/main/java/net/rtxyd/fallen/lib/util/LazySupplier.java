package net.rtxyd.fallen.lib.util;

import java.util.function.Supplier;

public class LazySupplier<T> implements Supplier<T> {
    Supplier<T> supplier;
    T data;

    public LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (data == null) {
            data = supplier.get();
        }
        return data;
    }
}