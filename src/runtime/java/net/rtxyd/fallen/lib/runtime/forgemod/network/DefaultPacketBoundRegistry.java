package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.GameLifecycleHelper;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.runtime.forgemod.util.TriFunction;
import net.rtxyd.fallen.lib.util.call.ContextKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class DefaultPacketBoundRegistry<E extends ICodecProvider<E>> extends AbstractPacketBoundRegistry<E, DefaultRegistryBoundPacketPayload.Begin, DefaultRegistryBoundPacketPayload<E>, DefaultRegistryBoundPacketPayload.End> {
    protected FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload<E>> defaultBufCodec;
    private static final Map<String, DefaultPacketBoundRegistry<?>> DEFAULT_SINGLETONS_BY_PATH = new HashMap<>();
    private static final Map<FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload<?>>, DefaultPacketBoundRegistry<?>> DEFAULT_SINGLETONS = new HashMap<>();
    static final ContextKey<FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload<?>>> CODEC_CONTEXT_KEY = GameLifecycleHelper.registerContextKey("fallen_lib.registry.default_bound");

    public static final Logger LOGGER = LogManager.getLogger();
    public DefaultPacketBoundRegistry(Logger logger, String path, String type, Predicate<ResourceLocation> locFilter, boolean doSync, boolean useTypeIdAsKey) {
        super(logger, path, type, locFilter, doSync, useTypeIdAsKey);
        this.initSingletonBoundCodec();
        this.defaultBufCodec = createDefaultByteBufCodec(LOGGER, DefaultRegistryBoundPacketPayload::new);
    }

    @SuppressWarnings("unchecked")
    public static <E extends ICodecProvider<E>> DefaultPacketBoundRegistry<E> getDefaultSingleton(FriendlyByteBufCodec<?> bufCodec) {
        return (DefaultPacketBoundRegistry<E>) DEFAULT_SINGLETONS.get(bufCodec);
    }

    @SuppressWarnings("unchecked")
    public static <E extends ICodecProvider<E>> DefaultPacketBoundRegistry<E> getDefaultSingletonByPath(String path) {
        return (DefaultPacketBoundRegistry<E>) DEFAULT_SINGLETONS_BY_PATH.get(path);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void registerDefaultSingleton(DefaultPacketBoundRegistry instance) {
        DEFAULT_SINGLETONS.putIfAbsent(instance.getDefaultBufCodec(), instance);
        DEFAULT_SINGLETONS_BY_PATH.putIfAbsent(instance.path, instance);
    }

    @Override
    public final void syncClient(OnDatapackSyncEvent e) {
        throw new UnsupportedOperationException("Registry[" + this.getClass() + "] should be synchronized in default process!");
    }

    public void handleBegin(Supplier<NetworkEvent.Context> contextSupplier) {
        super.handleBegin(contextSupplier);
    }

    public void handleProcess(Supplier<NetworkEvent.Context> contextSupplier, ResourceLocation path, E item) {
        super.handleProcess(contextSupplier, path, item);
    }

    public void handleEnd(Supplier<NetworkEvent.Context> contextSupplier) {
        super.handleEnd(contextSupplier);
    }

    static void registerSyncDefault() {
        MinecraftForge.EVENT_BUS.addListener(DefaultSyncer::syncDefault);
    }

    @Override
    public boolean validate() {
        if (this.defaultBufCodec == null && this.doSync) {
            throw new UnsupportedOperationException("Registry [" + this.getClass() + "] is intended to do sync, but bound bufCodec is not initialized!");
        }
        if (!DEFAULT_SINGLETONS.containsKey(this.defaultBufCodec)) {
            throw new UnsupportedOperationException("Registry [" + this.getClass() +  "] is intended to do reload, but it's not registered!");
        }
        return true;
    }

    static class DefaultSyncer {
        // serverside
        @SuppressWarnings({"unchecked", "rawtypes"})
        public static void syncDefault(OnDatapackSyncEvent e) {
            ServerPlayer player = e.getPlayer();
            PacketDistributor.PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);
            DEFAULT_SINGLETONS.forEach((bufCodec, reg) -> {
                try {
                    GameLifecycleHelper.submitContextCall(CODEC_CONTEXT_KEY, () -> bufCodec);
                    Connection.sendToTarget(target, new DefaultRegistryBoundPacketPayload.Begin(reg.path));
                    reg.registry.forEach((path, item) -> {
                        Connection.sendToTarget(target, new DefaultRegistryBoundPacketPayload(path, item, reg.path));
                    });
                    Connection.sendToTarget(target, new DefaultRegistryBoundPacketPayload.End(reg.path));
                } finally {
                    GameLifecycleHelper.callAndRemoveIfPresent(CODEC_CONTEXT_KEY, GameLifecycleHelper.EMPTY_EX_CONSUMER);
                }
            });
        }
    }

    public FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload<E>> createDefaultByteBufCodec(
            Logger logger,
            TriFunction<ResourceLocation, E, String, DefaultRegistryBoundPacketPayload<E>> constructor
    ) {
        if (this.defaultBufCodec == null) {
            this.defaultBufCodec = new FriendlyByteBufCodec<>() {
                @Override
                public void encode(@NotNull DefaultRegistryBoundPacketPayload<E> value, @NotNull FriendlyByteBuf buf) {
                    String regPath = value.getRegistryPath();
                    buf.writeInt(regPath.length());
                    buf.writeUtf(regPath);
                    buf.writeResourceLocation(value.getPath());
                    buf.writeNbt((CompoundTag) getCodec().encodeStart(NbtOps.INSTANCE, value.getItem())
                            .getOrThrow(false,s -> logger.error("Failed parsing item for {}", value.getItem())));
                }

                @Override
                public @NotNull DefaultRegistryBoundPacketPayload<E> decode(@NotNull FriendlyByteBuf buf) {
                    int length = buf.readInt();
                    String regPath = buf.readUtf(length);
                    ResourceLocation path = buf.readResourceLocation();
                    CompoundTag tag = buf.readNbt();
                    var result = getCodec().decode(NbtOps.INSTANCE, tag)
                            .getOrThrow(false,s -> logger.error("Failed parsing received payload for {}", path)).getFirst();
                    return constructor.apply(path, result, regPath);
                }
            };
        }
        return this.defaultBufCodec;
    }

    public FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload<E>> getDefaultBufCodec() {
        return this.defaultBufCodec;
    }
}
