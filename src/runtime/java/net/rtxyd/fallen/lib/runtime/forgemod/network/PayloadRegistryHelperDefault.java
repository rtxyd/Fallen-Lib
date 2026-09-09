package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;

public class PayloadRegistryHelperDefault<E extends ICodecProvider<E>> implements IPayloadRegisterHelper {
    DefaultPacketBoundRegistry<E> registrySingleton;
    int sortPriority = 0;

    public PayloadRegistryHelperDefault(DefaultPacketBoundRegistry<E> registrySingleton, int sortPriority) {
        this.registrySingleton = registrySingleton;
        this.sortPriority = sortPriority;
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
