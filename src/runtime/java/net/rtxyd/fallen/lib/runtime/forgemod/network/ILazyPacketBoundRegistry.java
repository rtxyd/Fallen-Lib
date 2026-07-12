package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.NetworkEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface ILazyPacketBoundRegistry<E> {
    void beginReload();

    void onReload();

    // serverside
    void syncClient(OnDatapackSyncEvent e);

    void beginSync();

    void registerTempEntry(ResourceLocation loc, Supplier<E> item);

    void validateItem(ResourceLocation loc, E item);

    void handleBegin(Supplier<NetworkEvent.Context> contextSupplier);

    void handleProcess(Supplier<NetworkEvent.Context> contextSupplier, ResourceLocation path, Supplier<E> item);

    void handleEnd(Supplier<NetworkEvent.Context> contextSupplier);

    void applyTemp();

    record Constructors3Special<REGISTRY_ITEM extends ICodecProvider<REGISTRY_ITEM>,
            BEGIN extends LazyRegistryBoundPacketPayLoad.IBegin,
            PROCESS extends LazyRegistryBoundPacketPayLoad<REGISTRY_ITEM>,
            END extends LazyRegistryBoundPacketPayLoad.IEnd>(Supplier<BEGIN> beginConstructor,
                                                             BiFunction<ResourceLocation, Supplier<REGISTRY_ITEM>, PROCESS> processConstructor,
                                                             Supplier<END> endConstructor) {
    }
}
