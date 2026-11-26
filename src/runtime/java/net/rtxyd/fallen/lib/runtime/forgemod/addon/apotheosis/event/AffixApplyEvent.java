package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.event;

import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.affix.ItemAffixes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/**
 * Fires both sides.
 * Fires when {@link dev.shadowsoffire.apotheosis.affix.AffixHelper#applyAffix(ItemStack, AffixInstance)}
 */
public class AffixApplyEvent extends Event {
    private AffixInstance affix;
    private final ItemStack stack;
    private final ItemAffixes.Builder builder;

    public AffixApplyEvent(ItemStack stack, AffixInstance affix, ItemAffixes.Builder builder) {
        this.affix = affix;
        this.stack = stack;
        this.builder = builder;
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

    public ItemAffixes.Builder getBuilder() {
        return builder;
    }
}
