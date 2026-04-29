package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.world.item.ItemStack;
import net.rtxyd.fallen.lib.util.FinalObjectCaky;
import net.rtxyd.fallen.lib.util.IObjectCaky;

public interface ItemStackCakyHandler {
    public static <T> T resolve(ItemStack stack, String key, IObjectCaky.CakyLoader<ItemStack, T> loader, IObjectCaky.CakyReviewer<ItemStack> reviewer) {
        return ((ItemStackCakyHandler)(Object)stack).fallen_lib$getObjectCaky(key, loader, reviewer);
    }
    public static <T> T resolveWith(ItemStack stack, String key, IObjectCaky.Type type, IObjectCaky.CakyLoader<ItemStack, T> loader, IObjectCaky.CakyReviewer<ItemStack> reviewer) {
        return ((ItemStackCakyHandler)(Object)stack).fallen_lib$getObjectCakyWith(key, type, loader, reviewer);
    }
    <T> T fallen_lib$getObjectCaky(String key, IObjectCaky.CakyLoader<ItemStack, T> loader, IObjectCaky.CakyReviewer<ItemStack> reviewer);
    <T> T fallen_lib$getObjectCakyWith(String key, IObjectCaky.Type type, IObjectCaky.CakyLoader<ItemStack, T> loader, IObjectCaky.CakyReviewer<ItemStack> reviewer);
}
