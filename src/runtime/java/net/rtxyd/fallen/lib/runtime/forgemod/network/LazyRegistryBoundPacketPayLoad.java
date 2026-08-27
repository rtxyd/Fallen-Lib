package net.rtxyd.fallen.lib.runtime.forgemod.network;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.util.LazySupplier;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class LazyRegistryBoundPacketPayLoad<T extends ICodecProvider<T>> implements IVanillaLikeCustomPacketPayload {
    public final ResourceLocation path;
    public final Supplier<T> item;
    @SuppressWarnings("rawtypes")
    private static final Map<Type<? extends LazyRegistryBoundPacketPayLoad>, AbstractLazyPacketBoundRegistry> REGISTRY_SINGLETONS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <A extends ICodecProvider<A>, B extends LazyRegistryBoundPacketPayLoad.IBegin, C extends LazyRegistryBoundPacketPayLoad<A>, D extends LazyRegistryBoundPacketPayLoad.IEnd>
    AbstractLazyPacketBoundRegistry<A, B, C, D> getBoundRegistry(Type<?> registryType) {
        return (AbstractLazyPacketBoundRegistry<A, B, C, D>) REGISTRY_SINGLETONS.get(registryType);
    }

    static <A extends ICodecProvider<A>, B extends LazyRegistryBoundPacketPayLoad.IBegin, C extends LazyRegistryBoundPacketPayLoad<A>, D extends LazyRegistryBoundPacketPayLoad.IEnd>
    void boundRegistrySingleton(Type<? extends LazyRegistryBoundPacketPayLoad<A>> packetType, AbstractLazyPacketBoundRegistry<A, B, C ,D> instance) {
        if (REGISTRY_SINGLETONS.putIfAbsent(packetType, instance) != null) {
            throw new UnsupportedOperationException("Payload " + packetType.id() + " is already bound to a registry singleton!");
        }
    }

    public Supplier<T> getItem() {
        return item;
    }

    public ResourceLocation getPath() {
        return path;
    }

    public static <ORIGIN extends LazyRegistryBoundPacketPayLoad<E>, E extends ICodecProvider<E>> FriendlyByteBufCodec<ORIGIN> createLazyByteBufCodec(
            Logger logger,
            Codec<E> itemCodec,
            BiFunction<ResourceLocation, Supplier<E>, ORIGIN> constructor
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
                Supplier<E> result = new LazySupplier<>(() -> itemCodec.decode(NbtOps.INSTANCE, tag)
                        .getOrThrow().getFirst());
                return constructor.apply(path, result);
            }
        };
    }

    public LazyRegistryBoundPacketPayLoad(ResourceLocation path, Supplier<T> item) {
        this.path = path;
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(IPayloadContext contextSupplier) {
        ((AbstractLazyPacketBoundRegistry<T, ?, ?, ?>)getBoundRegistry(this.type())).handleProcess(contextSupplier, this.getPath(), this.getItem());
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
