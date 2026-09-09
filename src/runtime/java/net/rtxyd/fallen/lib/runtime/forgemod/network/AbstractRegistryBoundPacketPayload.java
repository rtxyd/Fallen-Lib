package net.rtxyd.fallen.lib.runtime.forgemod.network;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class AbstractRegistryBoundPacketPayload<E extends ICodecProvider<E>> implements IVanillaLikeCustomPacketPayload {
    private final ResourceLocation path;
    private final E registryItem;
    @SuppressWarnings("rawtypes")
    private static final Map<Type<? extends AbstractRegistryBoundPacketPayload>, AbstractPacketBoundRegistry> REGISTRY_SINGLETONS = new HashMap<>();

    protected AbstractRegistryBoundPacketPayload(ResourceLocation path, E registryItem) {
        this.path = path;
        this.registryItem = registryItem;
    }

    @SuppressWarnings("unchecked")
    protected Codec<E> getBoundItemCodec() {
        var registry = getBoundRegistry(this.type());
        if (registry == null) {
            throw new RuntimeException(String.format("Packet [%s] registry is not bound!", this.getClass()));
        }
        return (Codec<E>) registry.getCodec();
    }

    public final ResourceLocation getPath() {
        return path;
    }
    public final E getItem() {
        return registryItem;
    }

    @SuppressWarnings("unchecked")
    public void handle(IPayloadContext contextSupplier) {
        ((AbstractPacketBoundRegistry<E, ?, ?, ?>)getBoundRegistry(this.type())).handleProcess(contextSupplier, this.getPath(), this.getItem());
    }

    static <A extends ICodecProvider<A>, B extends AbstractRegistryBoundPacketPayload.IBegin, C extends AbstractRegistryBoundPacketPayload<A>, D extends AbstractRegistryBoundPacketPayload.IEnd>
    void boundRegistrySingleton(Type<? extends AbstractRegistryBoundPacketPayload<A>> packetType, AbstractPacketBoundRegistry<A, B, C ,D> instance) {
        if (REGISTRY_SINGLETONS.putIfAbsent(packetType, instance) != null) {
            throw new UnsupportedOperationException("Payload " + packetType.id() + " is already bound to a registry singleton!");
        }
    }

    @SuppressWarnings("unchecked")
    public static <A extends ICodecProvider<A>, B extends AbstractRegistryBoundPacketPayload.IBegin, C extends AbstractRegistryBoundPacketPayload<A>, D extends AbstractRegistryBoundPacketPayload.IEnd>
    AbstractPacketBoundRegistry<A, B, C, D> getBoundRegistry(Type<?> registryType) {
        return (AbstractPacketBoundRegistry<A, B, C, D>) REGISTRY_SINGLETONS.get(registryType);
    }

    public static <ORIGIN extends AbstractRegistryBoundPacketPayload<E>, E extends ICodecProvider<E>> FriendlyByteBufCodec<ORIGIN> createByteBufCodec(
            Logger logger,
            Codec<E> itemCodec,
            BiFunction<ResourceLocation, E , ORIGIN> constructor
    ) {
        return new FriendlyByteBufCodec<>() {
            @Override
            public void encode(@NotNull FriendlyByteBuf buf, @NotNull ORIGIN value) {
                buf.writeResourceLocation(value.getPath());
                buf.writeNbt((CompoundTag) itemCodec.encodeStart(NbtOps.INSTANCE, value.getItem())
                        .getOrThrow(s -> {
                            logger.error("Failed parsing item for {}", value.getItem());
                            return new RuntimeException(s);
                        }));
            }

            @Override
            public @NotNull ORIGIN decode(@NotNull FriendlyByteBuf buf) {
                ResourceLocation path = buf.readResourceLocation();
                CompoundTag tag = buf.readNbt();
                var result = itemCodec.decode(NbtOps.INSTANCE, tag)
                        .getOrThrow(s -> {
                            logger.error("Failed parsing received payload for {}", path);
                            return new RuntimeException(s);
                        }).getFirst();
                return constructor.apply(path, result);
            }
        };
    }

    public static interface IBegin extends IVanillaLikeCustomPacketPayload {
        @NotNull Type<?> getProcessType();

        @Override
        default void handle(IPayloadContext contextSupplier) {
            getBoundRegistry(getProcessType()).handleBegin(contextSupplier);
        }
    }

    public static interface IEnd extends IVanillaLikeCustomPacketPayload {
        @NotNull Type<?> getProcessType();

        @Override
        default void handle(IPayloadContext contextSupplier) {
            getBoundRegistry(getProcessType()).handleEnd(contextSupplier);
        }
    }
}
