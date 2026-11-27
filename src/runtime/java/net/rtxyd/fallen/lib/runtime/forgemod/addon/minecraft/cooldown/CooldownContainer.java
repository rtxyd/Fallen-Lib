package net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.cooldown;

import net.rtxyd.fallen.lib.util.ins_attr.InsAttributeModifier;

import java.util.*;

public class CooldownContainer {
    Map<String, CooldownAttribute> byId = new HashMap<>();

    public void put(String id, CooldownAttribute cooldownAttribute) {
        byId.put(id, cooldownAttribute);
    }

    public long getCooledTime(String id) {
        CooldownAttribute attr = byId.get(id);
        if (Objects.nonNull(attr)) {
            return attr.outputAlt();
        }
        return -1L;
    }

    public void applyModifier(String id, InsAttributeModifier modifier) {
        CooldownAttribute attr = byId.get(id);
        if (Objects.nonNull(attr)) {
            attr.addModifier(id, modifier);
        }
    }

    public void clear(String id) {
        byId.remove(id);
    }
}
