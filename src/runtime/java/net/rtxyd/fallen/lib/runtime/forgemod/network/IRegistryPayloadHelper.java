package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.neoforged.bus.api.EventPriority;

public interface IRegistryPayloadHelper {
    IRegistryPayloadHelper setEventPriority(EventPriority eventPriority);

    IRegistryPayloadHelper setSortPriority(int sortPriority);

    boolean isLazy();
    void register();
    void initSingleton();
    int getSortPriority();
}
