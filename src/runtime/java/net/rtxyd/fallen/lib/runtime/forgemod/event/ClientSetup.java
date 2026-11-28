package net.rtxyd.fallen.lib.runtime.forgemod.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;

@EventBusSubscriber(
        modid = FallenLib.MODID,
        value = {Dist.CLIENT}
)
public class ClientSetup {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
    }
}
