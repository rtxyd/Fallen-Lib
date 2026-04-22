package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.world.item.ItemStack;
import net.rtxyd.fallen.lib.util.ObjectCaky;

public interface ItemStackCakyHandler {
    public static <T> T resolve(ItemStack stack, String key, ObjectCaky.CakyLoader<ItemStack, T> loader, ObjectCaky.CakyReviewer<ItemStack> reviewer) {
        return ((ItemStackCakyHandler)(Object)stack).fallen_lib$getObjectCaky(key, loader, reviewer);
    }
    <T> T fallen_lib$getObjectCaky(String key, ObjectCaky.CakyLoader<ItemStack, T> loader, ObjectCaky.CakyReviewer<ItemStack> reviewer);
}
