package net.rtxyd.fallen.lib.runtime.forgemod.mixin;

import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ILocalRarity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootRarity.class)
public class LootRarityMixin implements ILocalRarity {
    @Override
    public ResourceLocation fallen_lib$getId() {
        return RarityRegistry.INSTANCE.getKey((LootRarity) (Object)this);
    }
}
