package net.rtxyd.fallen.lib.runtime.forgemod;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.network.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(FallenLib.MODID)
public class FallenLib {
    public static final String MODID = "fallen_lib";
    public static final Logger LOGGER = LogManager.getLogger("fallen_lib");
    public FallenLib(IEventBus bus, ModContainer modContainer) {
        LOGGER.info("Fallen lib init");
        bus.addListener(EventPriority.HIGH, this::init);
        bus.addListener(EventPriority.NORMAL, Connection::init);
        bus.addListener(this::complete);
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public void init(FMLCommonSetupEvent e) {
        stage = Stage.LOADING;
        e.enqueueWork(() -> {
        });
    }

    public void complete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
        });
        stage = Stage.COMPLETE;
    }

    private static Stage stage;


    public static Stage getStage() {
        return stage;
    }

    public enum Stage {
        LOADING,
        COMPLETE
    }
}
