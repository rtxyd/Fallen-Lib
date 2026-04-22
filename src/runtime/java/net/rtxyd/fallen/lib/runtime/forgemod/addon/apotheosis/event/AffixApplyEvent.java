package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.event;

import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fires both sides.
 * Fires when {@link dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper#applyAffix(ItemStack, AffixInstance)}
 */
public class AffixApplyEvent extends Event {
    private AffixInstance affix;
    private final ItemStack stack;
    public AffixApplyEvent(ItemStack stack, AffixInstance affix) {
        this.affix = affix;
        this.stack = stack;
    }
    public ItemStack getItem() {
        return stack;
    }

    public AffixInstance getAffix() {
        return affix;
    }

    public void setAffix(AffixInstance affix) {
        this.affix = affix;
    }
}
