package net.rtxyd.fallen.lib.runtime.forgemod.addon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.cooldown.ICooldownHandler;
import net.rtxyd.fallen.lib.util.ins_attr.InsAttributeModifier;

public final class Cooldowns {

    public static final String KEY = "fallen._cooldown";

    private Cooldowns() {}

    public static boolean isOnCooldown(ResourceLocation id, LivingEntity entity) {
        ICooldownHandler handler = entity.getData(Common.AttachmentTypes.CD_HANDLER);
        return handler.isOnCooldown(createKey(id, ""), entity.level().getGameTime());
    }

    public static void startCooldown(ResourceLocation id, LivingEntity entity, int cooldown) {
        startCooldownWith(id, entity, cooldown, true);
    }

    public static void startCooldownWith(ResourceLocation id, LivingEntity entity, int cooldown, boolean isModifiable) {
        ICooldownHandler handler = entity.getData(Common.AttachmentTypes.CD_HANDLER);
        handler.startCooldown(createKey(id, ""), entity.level().getGameTime(), cooldown, isModifiable);
    }

    public static long getRemaining(ResourceLocation id, LivingEntity entity) {
        ICooldownHandler handler = entity.getData(Common.AttachmentTypes.CD_HANDLER);
        return handler.getRemaining(createKey(id, ""), entity.level().getGameTime());
    }

    public static void applyModifier(ResourceLocation id, LivingEntity entity, InsAttributeModifier modifier) {
        ICooldownHandler handler = entity.getData(Common.AttachmentTypes.CD_HANDLER);
        handler.applyModifier(createKey(id, ""), modifier);
    }

    public static void clear(ResourceLocation id, LivingEntity entity) {
        ICooldownHandler handler = entity.getData(Common.AttachmentTypes.CD_HANDLER);
        handler.clear(createKey(id, ""));
    }

    public static String createKey(ResourceLocation id, String prefix) {
        return prefix + KEY + id.toShortLanguageKey();
    }
}