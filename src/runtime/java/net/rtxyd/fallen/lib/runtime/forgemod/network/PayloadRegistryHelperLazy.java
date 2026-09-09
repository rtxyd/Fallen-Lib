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

public class PayloadRegistryHelperLazy<I extends ICodecProvider<I>,
        PB extends LazyRegistryBoundPacketPayLoad.IBegin,
        P extends LazyRegistryBoundPacketPayLoad<I>,
        PE extends LazyRegistryBoundPacketPayLoad.IEnd,
        R extends AbstractLazyPacketBoundRegistry<I, PB, P, PE>> implements IPayloadRegisterHelper {
    R registrySingleton;
    PayloadDefinition<PB> beginDef;
    Supplier<PB> beginConstructor;

    PayloadDefinition<P> processDef;
    BiFunction<ResourceLocation, Supplier<I>, P> processConstructor;

    PayloadDefinition<PE> endDef;
    Supplier<PE> endConstructor;

    EventPriority eventPriority;
    int sortPriority = 0;

    public PayloadRegistryHelperLazy(R registrySingleton,
                                 PayloadDefinition<PB> beginDef, Supplier<PB> beginConstructor,
                                 PayloadDefinition<P> processDef, BiFunction<ResourceLocation, Supplier<I>, P> processConstructor,
                                 PayloadDefinition<PE> endDef, Supplier<PE> endConstructor,
                                 EventPriority eventPriority) {
        this.registrySingleton = registrySingleton;
        this.beginDef = beginDef;
        this.beginConstructor = beginConstructor;
        this.processConstructor = processConstructor;
        this.processDef = processDef;
        this.endDef = endDef;
        this.endConstructor = endConstructor;
        this.eventPriority = eventPriority;
    }

    public ILazyPacketBoundRegistry.Constructors3Special<I, PB, P, PE> constructors3() {
        return new ILazyPacketBoundRegistry.Constructors3Special<>(beginConstructor, processConstructor, endConstructor);
    }

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public void register() {
        NetworkRegistry.register(beginDef.type(), beginDef.codec(), beginDef.handler(), List.of(ConnectionProtocol.PLAY), Optional.of(PacketFlow.CLIENTBOUND), Connection.VERSION, false);
        NetworkRegistry.register(processDef.type(), processDef.codec(), processDef.handler(), List.of(ConnectionProtocol.PLAY), Optional.of(PacketFlow.CLIENTBOUND), Connection.VERSION, false);
        NetworkRegistry.register(endDef.type(), endDef.codec(), endDef.handler(), List.of(ConnectionProtocol.PLAY), Optional.of(PacketFlow.CLIENTBOUND), Connection.VERSION, false);
    }

    @Override
    public void initSingleton() {
        registrySingleton.registerCommon();
        registrySingleton.initPacketsConstructors(constructors3());
        AbstractLazyPacketBoundRegistry.registerSingleton(registrySingleton);
        LazyRegistryBoundPacketPayLoad.boundRegistrySingleton(processDef.type(), registrySingleton);
        registrySingleton.registerSyncWithPriority(eventPriority);
    }

    @Override
    public int getSortPriority() {
        return 0;
    }
}
