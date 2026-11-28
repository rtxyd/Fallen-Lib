package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;

@EventBusSubscriber(
        modid = FallenLib.MODID,
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
