package net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.mob_effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public interface IMobEffectContext {
    Holder<MobEffect> getEffect();
    int getAmplifier();
}
