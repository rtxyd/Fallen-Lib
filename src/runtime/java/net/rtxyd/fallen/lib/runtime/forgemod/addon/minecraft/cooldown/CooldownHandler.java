package net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.cooldown;

import net.rtxyd.fallen.lib.util.ins_attr.InsAttributeModifier;

public class CooldownHandler implements ICooldownHandler {
    final CooldownContainer container;

    public CooldownHandler() {
        this.container = new CooldownContainer();
    }

    @Override
    public void startCooldown(String id, long gameTime, int cooldown, boolean isModifiable) {
        if (isModifiable) {
            startFlex(id, gameTime, cooldown);
        } else {
            startUnmodifiable(id, gameTime, cooldown);
        }
    }

    private void startUnmodifiable(String id, long gameTime, int cooldown) {
        container.put(id, CooldownAttribute.ofUnmodifiable(id, gameTime, cooldown));
    }

    private void startFlex(String id, long gameTime, int cooldown) {
        container.put(id, CooldownAttribute.of(id, gameTime, cooldown));
    }

    @Override
    public boolean isOnCooldown(String id, long gameTime) {
        return container.getCooledTime(id) >= gameTime;
    }

    @Override
    public void applyModifier(String id, InsAttributeModifier modifier) {
        container.applyModifier(id, modifier);
    }

    @Override
    public long getRemaining(String key, long gameTime) {
        long time = container.getCooledTime(key);
        if (time < 0) return -1;
        return time - gameTime;
    }

    @Override
    public void clear(String id) {
        container.clear(id);
    }
}
