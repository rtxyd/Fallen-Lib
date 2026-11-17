package net.rtxyd.fallen.lib.runtime.forgemod.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.rtxyd.fallen.lib.util.LazySupplier;

import java.util.function.Supplier;

public class SupplierCodec<E> implements Codec<Supplier<E>> {
    final Codec<E> codec;

    public SupplierCodec(Codec<E> codec) {
        this.codec = codec;
    }

    @Override
    public <T> DataResult<T> encode(Supplier<E> input, DynamicOps<T> ops, T prefix) {
        E e = input.get();
        return codec.encode(e, ops, prefix);
    }

    @Override
    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> ops, T input) {
        Supplier<E> supplier = new LazySupplier<>(() -> codec.decode(ops, input).getOrThrow().getFirst());
        return DataResult.success(Pair.of(supplier, input));
    }
}
