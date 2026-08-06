package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;

@Mod.EventBusSubscriber(
        modid = FallenLib.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
@OnlyIn(Dist.CLIENT)
final class UtilClientSetup {
    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            GameLifecycleHelper.pSupplier = () -> Minecraft.getInstance().player;
            Thread clientThread = Thread.currentThread();
            GameLifecycleHelper.tSupplier = () -> clientThread;
            GameLifecycleHelper.CALL_BOX.setThread(1, clientThread);
            GameLifecycleHelper.clientCycleTickCount = 0;
        });
    }
}
