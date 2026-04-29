package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.affix;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.event.AffixCacheRefreshEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ItemStackCakyHandler;
import net.rtxyd.fallen.lib.util.IObjectCaky;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default affix attribute helper
 */
public class DAffixAttributeHelper {
    public static final String DAFFIX_SYS = "fallen_lib:affix_attribute_system";
    private static final ThreadLocal<AtomicInteger> REFRESHER1 = ThreadLocal.withInitial(AtomicInteger::new);

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(DAffixAttributeHelper::refreshDAffixSys);
    }

    private static void nGetDSystem(ItemStack stack,
                                                    Map<DynamicHolder<? extends Affix>, AffixInstance> affixes) {
        DAffixAttributeSystem n = getDefaultAffixAttributeSystem(stack, affixes);
        ItemStackCakyHandler.resolveWith(stack, DAFFIX_SYS, IObjectCaky.Type.MANUAL, i -> n.updateWith(affixes), i -> REFRESHER1.get().incrementAndGet());
    }

    public static DAffixAttributeSystem getDefaultAffixAttributeSystem(ItemStack stack, Map<DynamicHolder<? extends Affix>, AffixInstance> affixes) {
        return ItemStackCakyHandler.resolveWith(stack, DAFFIX_SYS, IObjectCaky.Type.MANUAL,i -> new DAffixAttributeSystem(affixes), i -> REFRESHER1.get().get());
    }

    private static void refreshDAffixSys(AffixCacheRefreshEvent event) {
        REFRESHER1.get().incrementAndGet();
    }
}