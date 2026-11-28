package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class RegistryPayloadHelper<
        I extends ICodecProvider<I>,
        B extends IVanillaLikeCustomPacketPayload,
        P extends IVanillaLikeCustomPacketPayload,
        E extends IVanillaLikeCustomPacketPayload,
        R extends AbstractPacketBoundRegistry<I>
        > implements IRegistryPayloadHelper {

    R registrySingleton;

    PayloadDefinition<B> beginDef;
    Supplier<B> beginConstructor;

    PayloadDefinition<P> processDef;
    BiFunction<ResourceLocation, I, P> processConstructor;

    PayloadDefinition<E> endDef;
    Supplier<E> endConstructor;

    Constructors3<I> constructors3;

    EventPriority eventPriority = EventPriority.NORMAL;

    int sortPriority = 0;

    private RegistryPayloadHelper(R registrySingleton,
                                  PayloadDefinition<B> beginDef,
                                  Supplier<B> beginConstructor,
                                  PayloadDefinition<P> processDef,
                                  BiFunction<ResourceLocation, I, P> processConstructor,
                                  PayloadDefinition<E> endDef,
                                  Supplier<E> endConstructor) {
        this.registrySingleton = registrySingleton;
        this.beginDef = beginDef;
        this.beginConstructor = beginConstructor;
        this.processDef = processDef;
        this.processConstructor = processConstructor;
        this.endDef = endDef;
        this.endConstructor = endConstructor;
        this.constructors3 = new Constructors3<>(beginConstructor, processConstructor, endConstructor);
    }

    public static <
            I extends ICodecProvider<I>,
            B extends IVanillaLikeCustomPacketPayload,
            P extends IVanillaLikeCustomPacketPayload,
            E extends IVanillaLikeCustomPacketPayload,
            R extends AbstractPacketBoundRegistry<I>
            > IRegistryPayloadHelper of(R registrySingleton,
                                        PayloadDefinition<B> beginDef,
                                        Supplier<B> beginConstructor,
                                        PayloadDefinition<P> processDef,
                                        BiFunction<ResourceLocation, I, P> processConstructor,
                                        PayloadDefinition<E> endDef,
                                        Supplier<E> endConstructor) {
        return new RegistryPayloadHelper<>(registrySingleton,beginDef, beginConstructor, processDef, processConstructor, endDef, endConstructor);
    }

    public static <E extends ICodecProvider<E>> IRegistryPayloadHelper ofDefault(DefaultPacketBoundRegistry<E> registrySingleton) {
        return new Default<>(registrySingleton);
    }

    public static <
            I extends ICodecProvider<I>,
            P extends IVanillaLikeCustomPacketPayload,
            R extends AbstractPacketBoundRegistry<I>
            > IRegistryPayloadHelper
    ofSimple(R registrySingleton, PayloadDefinition<P> processDef, BiFunction<ResourceLocation, I, P> processConstructor) {
        ResourceLocation processId = processDef.type().id();
        ResourceLocation beginId = ResourceLocation.parse(processId + "_begin");
        CustomPacketPayload.Type<AbstractRegistryBoundPacketPayload.SimpleBegin> beginType = IVanillaLikeCustomPacketPayload.createType(beginId);
        AbstractRegistryBoundPacketPayload.SimpleBegin simpleBegin = new AbstractRegistryBoundPacketPayload.SimpleBegin(beginType, processDef.type());

        ResourceLocation endId = ResourceLocation.parse(processId + "_end");
        CustomPacketPayload.Type<AbstractRegistryBoundPacketPayload.SimpleEnd> endType = IVanillaLikeCustomPacketPayload.createType(endId);
        AbstractRegistryBoundPacketPayload.SimpleEnd simpleEnd = new AbstractRegistryBoundPacketPayload.SimpleEnd(endType, processDef.type());
        return new RegistryPayloadHelper<>(registrySingleton,
                new PayloadDefinition<>(beginType, StreamCodec.unit(simpleBegin), IVanillaLikeCustomPacketPayload::handle),
                () -> simpleBegin,
                processDef,
                processConstructor,
                new PayloadDefinition<>(endType, StreamCodec.unit(simpleEnd), IVanillaLikeCustomPacketPayload::handle),
                () -> simpleEnd);
    }

    @Override
    public IRegistryPayloadHelper setEventPriority(EventPriority eventPriority) {
        this.eventPriority = eventPriority;
        return this;
    }
    @Override
    public IRegistryPayloadHelper setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
        return this;
    }

    public Constructors3<I> getConstructors3() {
        return this.constructors3;
    }

    @Override
    public boolean isLazy() {
        return false;
    }

    @Override
    public void register() {
        NetworkRegistry.register(beginDef.type(), beginDef.codec(), beginDef.handler(), List.of(ConnectionProtocol.PLAY), Optional.of(PacketFlow.CLIENTBOUND), Connection.VERSION, false);
        NetworkRegistry.register(processDef.type(), processDef.codec(), processDef.handler(), List.of(ConnectionProtocol.PLAY), Optional.of(PacketFlow.CLIENTBOUND), Connection.VERSION, false);
        NetworkRegistry.register(endDef.type(), endDef.codec(), endDef.handler(), List.of(ConnectionProtocol.PLAY), Optional.of(PacketFlow.CLIENTBOUND), Connection.VERSION, false);
    }

    @Override
    public void initSingleton() {
        registrySingleton.initPacketsHandler(new IRegistrySyncHandler<I>() {
            @Override
            public void begin(@Nullable ServerPlayer player) {
                Connection.sendToPlayerOrAll(player, getConstructors3().beginConstructor().get());
            }

            @Override
            public void process(@Nullable ServerPlayer player, ResourceLocation path, I item) {
                Connection.sendToPlayerOrAll(player, getConstructors3().processConstructor().apply(path, item));
            }

            @Override
            public void end(@Nullable ServerPlayer player) {
                Connection.sendToPlayerOrAll(player, getConstructors3().endConstructor().get());
            }
        });
        registrySingleton.registerCommon();
        AbstractPacketBoundRegistry.registerSingleton(registrySingleton);
        AbstractRegistryBoundPacketPayload.boundRegistrySingleton(processDef.type(), registrySingleton);
        registrySingleton.registerSync(eventPriority);
    }

    @Override
    public int getSortPriority() {
        return sortPriority;
    }

    private static class Default<E extends ICodecProvider<E>> extends RegistryPayloadHelper<
                E,
                DefaultRegistryBoundPacketPayload.Begin,
                DefaultRegistryBoundPacketPayload<E>,
                DefaultRegistryBoundPacketPayload.End, DefaultPacketBoundRegistry<E>> {

        private Default(DefaultPacketBoundRegistry<E> registrySingleton) {
            super(registrySingleton, null, null, null, null, null, null);
        }

        @Override
        public boolean isLazy() {
            return false;
        }

        @Override
        public void register() {}

        @Override
        public void initSingleton() {
            registrySingleton.registerCommon();
            DefaultPacketBoundRegistry.registerDefaultSingleton(registrySingleton);
        }
        @Override
        public int getSortPriority() {
            return sortPriority;
        }
    }
}
