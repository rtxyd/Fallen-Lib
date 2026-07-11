package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.SimpleMixinConnector;
import net.rtxyd.fallen.lib.runtime.forgemod.compat.fga.FGAVersionStage;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;

import java.util.function.*;

public class Connection {
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(FallenLib.MODID, "netwwk"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel(); ;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    private static void registerApoth() {
        registerRegistryBoundSupplierPacketPayloadsWithPriority(ExtraGemBonusRegistry.INSTANCE, (FriendlyByteBufCodec) ExtraGemBonusPayload.BUF_CODEC,
                ExtraGemBonusPayload.Begin.class, ExtraGemBonusPayload.Begin::new, ExtraGemBonusPayload.Begin::handle,
                ExtraGemBonusPayload.class, ExtraGemBonusPayload::new, ExtraGemBonusPayload::handle,
                ExtraGemBonusPayload.End.class, ExtraGemBonusPayload.End::new, ExtraGemBonusPayload.End::handle,
                EventPriority.LOW);
    }

    private static <PB extends LazyPacketPayLoad.IBegin,
            P extends LazyPacketPayLoad<I>,
            I extends ICodecProvider<I>,
            PE extends LazyPacketPayLoad.IEnd,
            R extends AbstractLazyPacketBoundRegistry<I, PB, P, PE>>
    void registerRegistryBoundSupplierPacketPayloadsWithPriority(
            R singleton, FriendlyByteBufCodec<P> codec,
            Class<PB> begin, Supplier<PB> beginConstructor, BiConsumer<PB, Supplier<NetworkEvent.Context>> beginHandler,
            Class<P> process, BiFunction<ResourceLocation, Supplier<I>, P> processConstructor, BiConsumer<P, Supplier<NetworkEvent.Context>> processHandler,
            Class<PE> end, Supplier<PE> endConstructor, BiConsumer<PE, Supplier<NetworkEvent.Context>> endHandler,
            EventPriority priority) {
        if (INSTANCE == null) throw new RuntimeException("Fallen Lib Connection is not initialized!");

        singleton.registerCommon();

        INSTANCE.messageBuilder(begin, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(nullEncoderAuto())
                .decoder(t -> beginConstructor.get())
                .consumerMainThread(beginHandler).add();
        INSTANCE.messageBuilder(process, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(codec::encode)
                .decoder(codec::decode)
                .consumerMainThread(processHandler).add();
        INSTANCE.messageBuilder(end, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(nullEncoderAuto())
                .decoder(t -> endConstructor.get())
                .consumerMainThread(endHandler).add();
        singleton.initPacketsConstructors(new ILazyPacketBoundRegistry.Constructors3Special<>(beginConstructor, processConstructor, endConstructor));
        AbstractLazyPacketBoundRegistry.registerSingleton(singleton);
        LazyPacketPayLoad.boundRegistrySingleton(process, singleton);
        singleton.registerSync(priority);
    }

    public static void init(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            if (ModList.get().isLoaded("apotheosis")) {
                if (SimpleMixinConnector.FGACheck == null || !SimpleMixinConnector.FGACheck.getStage().equals(FGAVersionStage.FL_ONE_TWO)) {
                    FallenLib.LOGGER.info("Register fallen lib connection.");
                    Connection.registerApoth();
                }
            }
            registerInternal();
        });
    }

    public static <I extends ICodecProvider<I>> void registerDefaultPacketBoundRegistry(DefaultPacketBoundRegistry<I> singleton) {
        singleton.registerCommon();
        DefaultPacketBoundRegistry.registerDefaultSingleton(singleton);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerInternal() {
        if (INSTANCE == null) throw new RuntimeException("Fallen Lib Connection is not initialized!");

        INSTANCE.messageBuilder(DefaultRegistryBoundPacketPayload.Begin.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DefaultRegistryBoundPacketPayload.Begin.DEFAULT_BUF_CODEC::encode)
                .decoder(DefaultRegistryBoundPacketPayload.Begin.DEFAULT_BUF_CODEC::decode)
                .consumerMainThread(DefaultRegistryBoundPacketPayload.Begin::handle).add();

        INSTANCE.messageBuilder(DefaultRegistryBoundPacketPayload.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder((payload, buf) -> {
                    String regPath = payload.getRegPath();
                    var registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(regPath);
                    if (registry == null) return;
                    var codec = (FriendlyByteBufCodec) registry.getDefaultBufCodec();
                    if (codec == null) return;
                    codec.encode(payload, buf);
                })
                .decoder(buf -> {
                    buf.markReaderIndex();
                    int length = buf.readInt();
                    String regPath = buf.readUtf(length);
                    var registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(regPath);
                    if (registry == null) return DefaultRegistryBoundPacketPayload.EMPTY;
                    var codec = registry.getDefaultBufCodec();
                    if (codec == null) return DefaultRegistryBoundPacketPayload.EMPTY;
                    buf.resetReaderIndex();
                    return codec.decode(buf);
                })
                .consumerMainThread(DefaultRegistryBoundPacketPayload::handle).add();

        INSTANCE.messageBuilder(DefaultRegistryBoundPacketPayload.End.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DefaultRegistryBoundPacketPayload.End.DEFAULT_BUF_CODEC::encode)
                .decoder(DefaultRegistryBoundPacketPayload.End.DEFAULT_BUF_CODEC::decode)
                .consumerMainThread(DefaultRegistryBoundPacketPayload.End::handle).add();

        DefaultPacketBoundRegistry.registerSyncDefault();
    }

    public static <I extends ICodecProvider<I>,
            PB extends AbstractRegistryBoundPacketPayload.IBegin,
            P extends AbstractRegistryBoundPacketPayload<I>,
            PE extends AbstractRegistryBoundPacketPayload.IEnd,
            R extends AbstractPacketBoundRegistry<I, PB, P, PE>>
    void registerRegistryBoundPacketPayloads(R singleton, FriendlyByteBufCodec<P> codec,
                                             Class<PB> begin, Supplier<PB> beginConstructor, BiConsumer<PB, Supplier<NetworkEvent.Context>> beginHandler,
                                             Class<P> process, BiFunction<ResourceLocation, I, P> processConstructor, BiConsumer<P, Supplier<NetworkEvent.Context>> processHandler,
                                             Class<PE> end, Supplier<PE> endConstructor, BiConsumer<PE, Supplier<NetworkEvent.Context>> endHandler) {
        registerRegistryBoundPacketPayloadsWithPriority(singleton, codec,
                begin, beginConstructor, beginHandler,
                process, processConstructor, processHandler,
                end, endConstructor, endHandler,
                EventPriority.NORMAL);
    }

    public static <I extends ICodecProvider<I>,
            PB extends AbstractRegistryBoundPacketPayload.IBegin,
            P extends AbstractRegistryBoundPacketPayload<I>,
            PE extends AbstractRegistryBoundPacketPayload.IEnd,
            R extends AbstractPacketBoundRegistry<I, PB, P, PE>>
    void registerRegistryBoundPacketPayloadsWithPriority(R singleton, FriendlyByteBufCodec<P> codec,
                                             Class<PB> begin, Supplier<PB> beginConstructor, BiConsumer<PB, Supplier<NetworkEvent.Context>> beginHandler,
                                             Class<P> process, BiFunction<ResourceLocation, I, P> processConstructor, BiConsumer<P, Supplier<NetworkEvent.Context>> processHandler,
                                             Class<PE> end, Supplier<PE> endConstructor, BiConsumer<PE, Supplier<NetworkEvent.Context>> endHandler,
                                                         EventPriority priority) {
        if (INSTANCE == null) throw new RuntimeException("Fallen Lib Connection is not initialized!");

        singleton.registerCommon();

        INSTANCE.messageBuilder(begin, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(nullEncoderAuto())
                .decoder(t -> beginConstructor.get())
                .consumerMainThread(beginHandler).add();
        INSTANCE.messageBuilder(process, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(codec::encode)
                .decoder(codec::decode)
                .consumerMainThread(processHandler).add();
        INSTANCE.messageBuilder(end, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(nullEncoderAuto())
                .decoder(t -> endConstructor.get())
                .consumerMainThread(endHandler).add();
        singleton.initPacketsConstructors(new AbstractPacketBoundRegistry.Constructors3<>(beginConstructor, processConstructor, endConstructor));
        AbstractPacketBoundRegistry.registerSingleton(singleton);
        AbstractRegistryBoundPacketPayload.boundRegistrySingleton(process, singleton);
        singleton.registerSync(priority);
    }

    public static <I, P extends AbstractSingleEntryPacketPayLoad<I>> void registerSingleEntryPacketPayload(
            Class<P> process, NetworkDirection direction,
            FriendlyByteBufCodec<P> codec, BiConsumer<P, Supplier<NetworkEvent.Context>> handler) {
        INSTANCE.messageBuilder(process, id(), direction)
                .encoder(codec::encode)
                .decoder(codec::decode)
                .consumerMainThread(handler).add();
    }

    public static final Function<FriendlyByteBuf, ?> nullDecoder = t -> null;
    public static final BiConsumer<?, FriendlyByteBuf> nullEncoder = (a, b) -> {};

    @SuppressWarnings("unchecked")
    public static <T> Function<FriendlyByteBuf, T> nullDecoderAuto() {
        return (Function<FriendlyByteBuf, T>) nullDecoder;
    }
    @SuppressWarnings("unchecked")
    public static <T> BiConsumer<T, FriendlyByteBuf> nullEncoderAuto() {
        return (BiConsumer<T, FriendlyByteBuf>) nullEncoder;
    }

    public static <MSG> void sendToPlayer(MSG data, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), data);
    }

    public static <MSG> void sendToAllPlayers(MSG data) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), data);
    }

    public static <MSG> void sendToTarget(PacketDistributor.PacketTarget target, MSG data) {
        INSTANCE.send(target, data);
    }
}
