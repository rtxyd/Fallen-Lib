package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.rtxyd.fallen.lib.util.IObjectCaky;

public final class NBTFingerprints {

    public static IObjectCaky.CakyReviewer<ItemStack> fullNBT() {
        return stack -> {
            var tag = stack.getTag();
            return tag != null ? tag.hashCode() : Integer.MIN_VALUE;
        };
    }

    public static IObjectCaky.CakyReviewer<ItemStack> subTag(String key) {
        return stack -> {
            var tag = stack.getTagElement(key);
            return tag != null ? tag.hashCode() : Integer.MIN_VALUE;
        };
    }

    public static IObjectCaky.CakyReviewer<Entity> entityFullNBT() {
        return entity -> {
            var tag = entity.getPersistentData();
            return tag.hashCode();
        };
    }

    public static IObjectCaky.CakyReviewer<Entity> entitySubTag(String key) {
        return entity -> {
            var tag = entity.getPersistentData();
            var tag1 = tag.get(key);
            return tag1 != null ? tag1.hashCode() : Integer.MIN_VALUE;
        };
    }
}