package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.rtxyd.fallen.lib.util.IObjectCaky;

import java.util.Arrays;
import java.util.Objects;

public final class Fingerprints {

    public static IObjectCaky.CakyReviewer<ItemStack> fullComponents() {
        return stack -> {
            var tag = stack.getComponents();
            return tag.hashCode();
        };
    }

    public static IObjectCaky.CakyReviewer<ItemStack> components(DataComponentType<?>... keys) {
        return stack -> Arrays.hashCode(Arrays.stream(keys).map(stack::get).filter(Objects::nonNull).toArray());
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