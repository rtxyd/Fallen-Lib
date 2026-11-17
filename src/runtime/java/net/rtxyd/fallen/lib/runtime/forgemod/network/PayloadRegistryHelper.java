package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PayloadRegistryHelper<I extends ICodecProvider<I>,
        PB extends AbstractRegistryBoundPacketPayload.IBegin,
        P extends AbstractRegistryBoundPacketPayload<I>,
        PE extends AbstractRegistryBoundPacketPayload.IEnd,
        R extends AbstractPacketBoundRegistry<I, PB, P, PE>> implements IPayloadRegisterHelper {
    R registrySingleton;
    PayloadDefinition<PB> beginDef;
    Supplier<PB> beginConstructor;

    PayloadDefinition<P> processDef;
    BiFunction<ResourceLocation, I, P> processConstructor;

    PayloadDefinition<PE> endDef;
    Supplier<PE> endConstructor;

    EventPriority eventPriority;
    int sortPriority = 0;

    public PayloadRegistryHelper(R registrySingleton,
                                 PayloadDefinition<PB> beginDef, Supplier<PB> beginConstructor,
                                 PayloadDefinition<P> processDef, BiFunction<ResourceLocation, I, P> processConstructor,
                                 PayloadDefinition<PE> endDef, Supplier<PE> endConstructor,
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
    public IPacketBoundRegistry.Constructors3<I, PB, P, PE> constructors3() {
        return new IPacketBoundRegistry.Constructors3<>(beginConstructor, processConstructor, endConstructor);
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
        registrySingleton.initPacketsConstructors(constructors3());
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
