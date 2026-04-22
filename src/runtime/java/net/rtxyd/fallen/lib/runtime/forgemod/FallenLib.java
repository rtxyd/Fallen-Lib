package net.rtxyd.fallen.lib.runtime.forgemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.affix.DAffixAttributeHelper;
import net.rtxyd.fallen.lib.runtime.forgemod.compat.fga.FGAVersionStage;
import net.rtxyd.fallen.lib.runtime.forgemod.network.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(FallenLib.MODID)
public class FallenLib {
    public static final String MODID = "fallen_lib";
    public static final Logger LOGGER = LogManager.getLogger("fallen_lib");
    public FallenLib(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        bus.addListener(this::init);
        bus.addListener(this::complete);
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public void init(FMLCommonSetupEvent e) {
        stage = Stage.LOADING;
        e.enqueueWork(() -> {
            if (ModList.get().isLoaded("apotheosis")) {
                if (SimpleMixinConnector.FGACheck == null || !SimpleMixinConnector.FGACheck.getStage().equals(FGAVersionStage.FL_ONE_TWO)) {
                    FallenLib.LOGGER.info("Register fallen lib connection.");
                    Connection.register();
                }
                DAffixAttributeHelper.register();
            }
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
