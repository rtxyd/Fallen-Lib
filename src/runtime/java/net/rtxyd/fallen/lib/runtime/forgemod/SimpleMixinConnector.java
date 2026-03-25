package net.rtxyd.fallen.lib.runtime.forgemod;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class SimpleMixinConnector implements IMixinConnector {
    @Override
    public void connect() {
        boolean isApothExist = getClass().getClassLoader().getResource("dev/shadowsoffire/apotheosis/Apotheosis.class") != null;
        if (isApothExist) {
            Mixins.addConfiguration(FallenLib.MODID + ".mixins.json");
        }
    }
}
