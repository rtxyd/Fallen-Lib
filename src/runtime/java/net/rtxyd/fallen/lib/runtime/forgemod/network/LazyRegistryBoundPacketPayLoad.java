/*
package net.rtxyd.fallen.lib.runtime.forgemod.network;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.runtime.forgemod.util.LazyParser;
import net.rtxyd.fallen.lib.util.LazySupplier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class LazyRegistryBoundPacketPayLoad<
        I extends ICodecProvider<? super I>,
        T extends ICodecProvider<? super T> & Supplier<? super I>> implements IVanillaLikeCustomPacketPayload {
    public final ResourceLocation path;
    public final LazyParser<I> item;
    @SuppressWarnings("rawtypes")
    private static final Map<Type<? extends IVanillaLikeCustomPacketPayload>, AbstractLazyPacketBoundRegistry> REGISTRY_SINGLETONS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <
            I extends ICodecProvider<? super I>,
            A extends ICodecProvider<? super A> & Supplier<? super I>>
    AbstractLazyPacketBoundRegistry<I, A> getBoundRegistry(Type<?> registryType) {
        return (AbstractLazyPacketBoundRegistry<I, A>) REGISTRY_SINGLETONS.get(registryType);
    }

    static <
            I extends ICodecProvider<? super I>,
            A extends ICodecProvider<? super A> & Supplier<? super I>> void boundRegistrySingleton(
            Type<? extends IVanillaLikeCustomPacketPayload> packetType,
            AbstractLazyPacketBoundRegistry<I, A> instance) {
        if (REGISTRY_SINGLETONS.putIfAbsent(packetType, instance) != null) {
            throw new UnsupportedOperationException("Payload " + packetType.id() + " is already bound to a registry singleton!");
        }
    }

    public LazyParser<I> getItem() {
        return item;
    }

    public ResourceLocation getPath() {
        return path;
    }

    public static <
            I extends ICodecProvider<? super I>,
            ORIGIN extends LazyRegistryBoundPacketPayLoad<I, E>,
            E extends ICodecProvider<? super E> & Supplier<? super I>> FriendlyByteBufCodec<ORIGIN> createLazyByteBufCodec(
            Codec<I> itemCodec,
            BiFunction<ResourceLocation, Supplier<I>, ORIGIN> constructor
    ) {
        return new FriendlyByteBufCodec<>() {
            @Override
            public void encode(@NotNull FriendlyByteBuf buf, @NotNull ORIGIN value) {
                buf.writeResourceLocation(value.getPath());
                buf.writeNbt((CompoundTag) itemCodec.encodeStart(NbtOps.INSTANCE, value.getItem().get())
                        .getOrThrow());
            }

            @Override
            public @NotNull ORIGIN decode(@NotNull FriendlyByteBuf buf) {
                ResourceLocation path = buf.readResourceLocation();
                CompoundTag tag = buf.readNbt();
                Supplier<I> result = new LazySupplier<>(() -> itemCodec.decode(NbtOps.INSTANCE, tag)
                        .getOrThrow().getFirst());
                return constructor.apply(path, result);
            }
        };
    }

    public LazyRegistryBoundPacketPayLoad(ResourceLocation path, LazyParser<I> item) {
        this.path = path;
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(IPayloadContext contextSupplier) {
        ((AbstractLazyPacketBoundRegistry<I, T>)getBoundRegistry(this.type())).handleProcess(contextSupplier, this.getPath(), this.getItem());
    }

    public static interface IBegin extends IVanillaLikeCustomPacketPayload {
        @NotNull Type<?> getProcessType();

        default void handle(IPayloadContext contextSupplier) {
            getBoundRegistry(this.getProcessType()).handleBegin(contextSupplier);
        }
    }

    public static interface IEnd extends IVanillaLikeCustomPacketPayload {
        @NotNull Type<?> getProcessType();

        default void handle(IPayloadContext contextSupplier) {
            getBoundRegistry(this.getProcessType()).handleEnd(contextSupplier);
        }
    }
}
*/
