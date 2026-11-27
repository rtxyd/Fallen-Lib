package net.rtxyd.fallen.lib.runtime.forgemod.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.util.LazySupplier;

import java.util.function.Supplier;

public class LazyParser<I> extends LazySupplier<I> implements ICodecProvider<LazyParser<I>> {

    public Codec<LazyParser<I>> codec;
    public Codec<I> itemCodec;
    public DynamicOps<?> ops;
    public Object input;
    private boolean attempted = false;
    private I cachedValue;

    public <T> LazyParser(Codec<I> itemCodec, DynamicOps<T> ops, T input) {
        super(() -> itemCodec.decode(ops, input).getOrThrow().getFirst());
        this.itemCodec = itemCodec;
        this.ops = ops;
        this.input = input;
    }

    private LazyParser(Supplier<I> map) {
        super(map);
    }

    @Override
    public I get() {
        if (attempted) {
            return cachedValue;
        }

        attempted = true;

        try {
            cachedValue = super.get();
        } catch (RuntimeException e) {
            FallenLib.LOGGER.error("Failed to parse object", e);
        } finally {
            release();
        }
        return cachedValue;
    }

    private void release() {
        this.input = null;
        this.ops = null;
    }

    @Override
    public Codec<? extends LazyParser<I>> getCodec() {
        if (codec == null) {
            this.codec = new Codec<LazyParser<I>>() {
                @Override
                public <T> DataResult<Pair<LazyParser<I>, T>> decode(DynamicOps<T> ops, T input) {
                    return DataResult.success(Pair.of(new LazyParser<I>(itemCodec, ops, input), ops.empty()));
                }

                @Override
                public <T> DataResult<T> encode(LazyParser<I> input, DynamicOps<T> ops, T prefix) {
                    I item = input.get();
                    if (item == null) {
                        return DataResult.error(() -> "Failed parsing object: " + input);
                    }
                    return itemCodec.encode(item, ops, prefix);
                }
            };
            return codec;
        }
        return codec;
    }
}
