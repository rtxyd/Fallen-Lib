package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.event;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import java.util.Collections;
import java.util.Map;

/**
 * Fires both sides.
 * Fires when {@link dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper#getAffixes(ItemStack)} return
 */
public class AffixGetFinishEvent extends Event {
    private final ItemStack stack;
    private Map<DynamicHolder<? extends Affix>, AffixInstance> affixes;

    public AffixGetFinishEvent(ItemStack stack, Map<DynamicHolder<? extends Affix>, AffixInstance> affixes) {
        this.stack = stack;
        this.affixes = affixes;
    }

    public ItemStack getItem() {
        return stack;
    }

    public Map<DynamicHolder<? extends Affix>, AffixInstance> getAffixesView() {
        return affixes;
    }

    public void setTransientAffixes(Map<DynamicHolder<? extends Affix>, AffixInstance> affixes) {
        this.affixes = Collections.unmodifiableMap(affixes);
    }
}
