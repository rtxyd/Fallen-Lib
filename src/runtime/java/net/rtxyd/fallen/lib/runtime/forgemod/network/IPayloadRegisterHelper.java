package net.rtxyd.fallen.lib.runtime.forgemod.network;

public interface IPayloadRegisterHelper {
    boolean isLazy();
    void register();
    void initSingleton();
    int getSortPriority();
}
