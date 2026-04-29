package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.event;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * Fires both on client and server,
 * Fires before {@link AffixGetFinishEvent}
 */
public class AffixCacheRefreshEvent extends Event {
    private final Function<ItemStack, Map<DynamicHolder<? extends Affix>, AffixInstance>> deserializer;
    private Map<DynamicHolder<? extends Affix>, AffixInstance> affixes;
    private final ItemStack stack;

    public AffixCacheRefreshEvent(Function<ItemStack, Map<DynamicHolder<? extends Affix>, AffixInstance>> deserializer, ItemStack stack) {
        this.deserializer = deserializer;
        this.stack = stack;
    }

    public void setCacheAffixes(Map<DynamicHolder<? extends Affix>, AffixInstance> affixes) {
        this.affixes = Collections.unmodifiableMap(affixes);
    }

    public Map<DynamicHolder<? extends Affix>, AffixInstance> getAffixes() {
        if (affixes == null) {
            affixes = deserializer.apply(stack);
        }
        return affixes;
    }

    public ItemStack getItem() {
        return stack;
    }
}
