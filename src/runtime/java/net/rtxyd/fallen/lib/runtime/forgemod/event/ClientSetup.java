package net.rtxyd.fallen.lib.runtime.forgemod.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;

@Mod.EventBusSubscriber(
        modid = FallenLib.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ClientSetup {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
    }
}
