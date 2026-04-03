package net.rtxyd.fallen.lib.runtime.forgemod;

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
        boolean isApothExist = getClass().getClassLoader().getResource("dev/shadowsoffire/apotheosis/Apotheosis.class") != null;
        boolean isAttributeLibExist = getClass().getClassLoader().getResource("dev/shadowsoffire/attributeslib/AttributesLib.class") != null;
        if (isApothExist) {
            Mixins.addConfiguration(FallenLib.MODID + ".mixins.json");
        }
        if (isAttributeLibExist) {
            Mixins.addConfiguration(FallenLib.MODID + ".attrlib.mixins.json");
        }
    }

    private void connectRegistry() {
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
