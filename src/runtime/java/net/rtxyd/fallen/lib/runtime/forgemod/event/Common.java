package net.rtxyd.fallen.lib.runtime.forgemod.event;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.ExtraGemBonusRegistry;

public class Common {

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        ExtraGemBonusRegistry.register(event);
    }
}
