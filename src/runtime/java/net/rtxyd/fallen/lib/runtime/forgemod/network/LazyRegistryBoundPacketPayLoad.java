package net.rtxyd.fallen.lib.runtime.forgemod.network;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.util.LazySupplier;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class LazyRegistryBoundPacketPayLoad<T extends ICodecProvider<T>> {
    public final ResourceLocation path;
    public final Supplier<T> item;
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends LazyRegistryBoundPacketPayLoad>, AbstractLazyPacketBoundRegistry> REGISTRY_SINGLETONS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <A extends ICodecProvider<A>, B extends LazyRegistryBoundPacketPayLoad.IBegin, C extends LazyRegistryBoundPacketPayLoad<A>, D extends LazyRegistryBoundPacketPayLoad.IEnd>
    AbstractLazyPacketBoundRegistry<A, B, C, D> getBoundRegistry(Class<?> registryClass) {
        return (AbstractLazyPacketBoundRegistry<A, B, C, D>) REGISTRY_SINGLETONS.get(registryClass);
    }

    static <A extends ICodecProvider<A>, B extends LazyRegistryBoundPacketPayLoad.IBegin, C extends LazyRegistryBoundPacketPayLoad<A>, D extends LazyRegistryBoundPacketPayLoad.IEnd>
    void boundRegistrySingleton(Class<? extends LazyRegistryBoundPacketPayLoad<A>> packetClass, AbstractLazyPacketBoundRegistry<A, B, C ,D> instance) {
        if (REGISTRY_SINGLETONS.putIfAbsent(packetClass, instance) != null) {
            throw new UnsupportedOperationException("Payload " + packetClass.getName() + " is already bound to a registry singleton!");
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
            public void encode(@NotNull ORIGIN value, @NotNull FriendlyByteBuf buf) {
                buf.writeResourceLocation(value.getPath());
                buf.writeNbt((CompoundTag) itemCodec.encodeStart(NbtOps.INSTANCE, value.getItem().get())
                        .getOrThrow(false,s -> logger.error("Failed parsing item for {}", value.getItem())));
            }

            @Override
            public @NotNull ORIGIN decode(@NotNull FriendlyByteBuf buf) {
                ResourceLocation path = buf.readResourceLocation();
                CompoundTag tag = buf.readNbt();
                Supplier<E> result = new LazySupplier<>(() -> itemCodec.decode(NbtOps.INSTANCE, tag)
                        .getOrThrow(false,s -> logger.error("Failed parsing received payload for {}", path)).getFirst());
                return constructor.apply(path, result);
            }
        };
    }

    public LazyRegistryBoundPacketPayLoad(ResourceLocation path, Supplier<T> item) {
        this.path = path;
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        ((AbstractLazyPacketBoundRegistry<T, ?, ?, ?>)getBoundRegistry(this.getClass())).handleProcess(contextSupplier, this.getPath(), this.getItem());
    }

    public static interface IBegin {
        Class<?> getProcessClass();

        static  Class<?> getProcessClassAuto(IBegin inst) {
            return inst.getProcessClass();
        }

        default void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            getBoundRegistry(getProcessClassAuto(this)).handleBegin(contextSupplier);
        }
    }

    public static interface IEnd {
        Class<?> getProcessClass();

        static Class<?> getProcessClassAuto(IEnd inst) {
            return inst.getProcessClass();
        }

        default void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            getBoundRegistry(getProcessClassAuto(this)).handleEnd(contextSupplier);
        }
    }
}
