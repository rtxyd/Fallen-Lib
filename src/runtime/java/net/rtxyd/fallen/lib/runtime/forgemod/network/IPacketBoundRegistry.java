package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IPacketBoundRegistry<I> {

    void beginReload(ReloadType type);

    void onReload(ReloadType server);

    // serverside
    void syncClient(OnDatapackSyncEvent e);

    void beginSync();

    void registerTempEntry(ResourceLocation loc, I extraGemBonus);

    void validateItem(ResourceLocation loc, I item);

    void handleBegin(IPayloadContext contextSupplier);

    void handleProcess(IPayloadContext contextSupplier, ResourceLocation path, I item);

    void handleEnd(IPayloadContext contextSupplier);

    void applyTemp();

    enum ReloadType {
        SERVER,
        INTEGRATED_CLIENT,
        DEDICATED_CLIENT;
    }
}
