package net.rtxyd.fallen.lib.runtime.forgemod;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.rtxyd.fallen.lib.extra.mixin.FallenMixinConnectorRegistry;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

import java.util.ArrayList;
import java.util.List;

public class SimpleMixinConnector implements IMixinConnector {
    @Override
    public void connect() {
        connectSelf();
        connectRegistry();
    }

    private void connectSelf() {
        Mixins.addConfiguration(FallenLib.MODID + ".mixins.json");
        conditionalMixins();
    }

    private void conditionalMixins() {
        boolean isApothExist = getClass().getClassLoader().getResource("dev/shadowsoffire/apotheosis/Apotheosis.class") != null;

        if (isApothExist) {
            Mixins.addConfiguration(FallenLib.MODID + ".apoth.mixins.json");
        }
    }

    private ModInfo findMod(String modId) {
        for (ModInfo mod : FMLLoader.getLoadingModList().getMods()) {
            if (mod.getModId().equals(modId)) {
                return mod;
            }
        }
        return null;
    }

    private void connectRegistry() {
        FallenLib.LOGGER.info("Prepare dedicated conditional mixins");
        List<IMixinConnector> connectors = new ArrayList<>();

        FallenMixinConnectorRegistry.forEach(cl -> {
            try {
                Class<?> clz = getClass().getClassLoader().loadClass(cl);
                connectors.add((IMixinConnector) clz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                FallenLib.LOGGER.error("Can't load mixin connector class {}", cl);
            }
        });
        for (IMixinConnector connector : connectors) {
            try {
                connector.connect();
            } catch (Exception e) {
                e.printStackTrace();
                FallenLib.LOGGER.error("Mixin connector [{}] failed to connect.", connector.getClass());
            }
        }
    }
}
