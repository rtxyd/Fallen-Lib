package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IPacketBoundRegistry<REGISTRY_ITEM> {

    void beginReload();

    void onReload();

    // serverside
    void syncClient(OnDatapackSyncEvent e);

    void beginSync();

    void registerTempEntry(ResourceLocation loc, REGISTRY_ITEM extraGemBonus);

    void validateItem(ResourceLocation loc, REGISTRY_ITEM item);

    void handleBegin(IPayloadContext contextSupplier);

    void handleProcess(IPayloadContext contextSupplier, ResourceLocation path, REGISTRY_ITEM item);

    void handleEnd(IPayloadContext contextSupplier);

    void applyTemp();

    public static record Constructors3<REGISTRY_ITEM extends ICodecProvider<REGISTRY_ITEM>,
    BEGIN extends AbstractRegistryBoundPacketPayload.IBegin,
    PROCESS extends AbstractRegistryBoundPacketPayload<REGISTRY_ITEM>,
    END extends AbstractRegistryBoundPacketPayload.IEnd>(Supplier<BEGIN> beginConstructor, BiFunction<ResourceLocation, REGISTRY_ITEM, PROCESS> processConstructor, Supplier<END> endConstructor) {}
}
