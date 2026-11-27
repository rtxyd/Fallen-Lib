package net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.cooldown;


import net.rtxyd.fallen.lib.util.ins_attr.InsAttributeModifier;

public interface ICooldownHandler {
    void startCooldown(String id, long gameTime, int cooldown, boolean flag);

    boolean isOnCooldown(String id, long gameTime);

    void clear(String id);

    void applyModifier(String key, InsAttributeModifier modifier);

    long getRemaining(String key, long gameTime);
}
