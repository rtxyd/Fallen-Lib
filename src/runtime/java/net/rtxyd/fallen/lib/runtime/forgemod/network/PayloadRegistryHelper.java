package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PayloadRegistryHelper<
        I extends ICodecProvider<I>,
        P extends IVanillaLikeCustomPacketPayload,
        R extends AbstractPacketBoundRegistry<I>> implements IPayloadRegisterHelper {
    R registrySingleton;
    PayloadDefinition<P> beginDef;
    Supplier<P> beginConstructor;

    PayloadDefinition<P> processDef;
    BiFunction<ResourceLocation, I, P> processConstructor;

    PayloadDefinition<P> endDef;
    Supplier<P> endConstructor;

    EventPriority eventPriority;
    int sortPriority = 0;

    public PayloadRegistryHelper(R registrySingleton,
                                 PayloadDefinition<P> beginDef, Supplier<P> beginConstructor,
                                 PayloadDefinition<P> processDef, BiFunction<ResourceLocation, I, P> processConstructor,
                                 PayloadDefinition<P> endDef, Supplier<P> endConstructor,
                                 EventPriority eventPriority,
                                 int sortPriority) {
        this.registrySingleton = registrySingleton;
        this.beginDef = beginDef;
        this.beginConstructor = beginConstructor;
        this.processDef = processDef;
        this.processConstructor = processConstructor;
        this.endDef = endDef;
        this.endConstructor = endConstructor;
        this.eventPriority = eventPriority;
        this.sortPriority = sortPriority;
    }
    public Constructors3<I> constructors3() {
        return new Constructors3<>(beginConstructor, processConstructor, endConstructor);
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
            Constructors3<I> constructors3;

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

            public Constructors3<I> getConstructors3() {
                if (constructors3 == null) {
                    constructors3 = constructors3();
                    return constructors3;
                }
                return constructors3;
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
}
