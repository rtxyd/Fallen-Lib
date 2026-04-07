package net.rtxyd.fallen.lib.runtime.forgemod;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.rtxyd.fallen.lib.extra.mixin.FallenMixinConnectorRegistry;
import net.rtxyd.fallen.lib.runtime.forgemod.compat.fga.FGALegacyCheck;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.Restriction;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

import java.util.ArrayList;
import java.util.List;

public class SimpleMixinConnector implements IMixinConnector {
    public static FGALegacyCheck FGACheck;
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
        boolean isAttributeLibExist = getClass().getClassLoader().getResource("dev/shadowsoffire/attributeslib/AttributesLib.class") != null;

        ModInfo fga = findMod("fallen_gems_affixes");
        boolean isFGAExist = fga != null;

        boolean emmitAttrLibFix = true;
        boolean emmitExtraGemBonuses = true;

        if (isFGAExist) {
            FGACheck = FGACompat(fga);
            switch (FGACheck.getStage()) {
                case FL_ONE_TWO -> {
                    emmitExtraGemBonuses = false;
                }
                case FL_ONE_THREE_TWO -> {
                    emmitAttrLibFix = false;
                }
                case FL_ONE_THREE_THREE -> {}
            }
        }
        if (isApothExist) {
            Mixins.addConfiguration(FallenLib.MODID + ".categoryfix.mixins.json");
            if (emmitExtraGemBonuses) {
                Mixins.addConfiguration(FallenLib.MODID + ".extragembonuses.mixins.json");
            }
        }
        if (isAttributeLibExist) {
            if (emmitAttrLibFix) {
                Mixins.addConfiguration(FallenLib.MODID + ".attrlib.mixins.json");
            }
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

    private FGALegacyCheck FGACompat(ModInfo fga) {
        boolean oldVersion = false;
        boolean hasLib = false;
        if (fga != null) {
            for (IModInfo.ModVersion dependency : fga.getDependencies()) {
                if (dependency.getModId().equals("fallen_lib")) {
                    hasLib = true;
                    for (Restriction restriction : dependency.getVersionRange().getRestrictions()) {
                        if (restriction.getLowerBound().compareTo(new DefaultArtifactVersion("1.3.2")) > 0) {
                            oldVersion = true;
                        }
                    }
                }
            }
        }
        return new FGALegacyCheck(hasLib, oldVersion);
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
