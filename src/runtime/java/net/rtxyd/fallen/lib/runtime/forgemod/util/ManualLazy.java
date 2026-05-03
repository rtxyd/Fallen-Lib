package net.rtxyd.fallen.lib.runtime.forgemod.util;

import java.util.function.BiConsumer;

public class ManualLazy<H, T> {
    private final H holder;
    private final BiConsumer<H, T> valueSetter;

    public ManualLazy(H holder, BiConsumer<H, T> valueSetter) {
        this.holder = holder;
        this.valueSetter = valueSetter;
    }

    public H getHolder() {
        return holder;
    }

    public void setHolderValue(T value) {
        valueSetter.accept(holder, value);
    }
}
