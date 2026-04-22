package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.registry;

import net.minecraft.world.item.ItemStack;
import net.rtxyd.fallen.lib.util.ObjectCaky;

import java.util.ArrayList;
import java.util.List;

/**
 * Now not used.
 */
public final class AffixHashRegistry {
    private static final List<ObjectCaky.CakyReviewer<ItemStack>> LIST = new ArrayList<>();
    private static List<ObjectCaky.CakyReviewer<ItemStack>> listView;

    static void register(ObjectCaky.CakyReviewer<ItemStack> f) {
        if (f == null) throw new IllegalArgumentException("null function");
        LIST.add(f);
    }

    public static List<ObjectCaky.CakyReviewer<ItemStack>> snapshot() {
        if (listView == null) {
            listView = List.copyOf(LIST);
        }
        return listView;
    }
}