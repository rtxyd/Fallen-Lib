package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.registry;

import net.minecraft.world.item.ItemStack;
import net.rtxyd.fallen.lib.util.IObjectCaky;

import java.util.ArrayList;
import java.util.List;

/**
 * Now not used.
 */
public final class AffixHashRegistry {
    private static final List<IObjectCaky.CakyReviewer<ItemStack>> LIST = new ArrayList<>();
    private static List<IObjectCaky.CakyReviewer<ItemStack>> listView;

    static void register(IObjectCaky.CakyReviewer<ItemStack> f) {
        if (f == null) throw new IllegalArgumentException("null function");
        LIST.add(f);
    }

    public static List<IObjectCaky.CakyReviewer<ItemStack>> snapshot() {
        if (listView == null) {
            listView = List.copyOf(LIST);
        }
        return listView;
    }
}