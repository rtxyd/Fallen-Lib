package net.rtxyd.fallen.lib.runtime.forgemod.patch.inserters;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.rtxyd.fallen.lib.api.annotation.FallenInserter;
import net.rtxyd.fallen.lib.api.annotation.Notes;
import net.rtxyd.fallen.lib.api.annotation.Params;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.event.AffixApplyEvent;
import net.rtxyd.fallen.lib.type.util.patch.IInserterContext;
import net.rtxyd.fallen.lib.util.INull;
import net.rtxyd.fallen.lib.util.patch.InserterType;

import java.util.HashMap;

public class Inserters {
    @FallenInserter(value = InserterType.BEFORE_MODIFY_ARG, params = @Params(catchOuterArgs = {0}, modifyArg = 1))
    @Notes(argTypes = {ItemStack.class, DynamicHolder.class, AffixInstance.class}, receiverType = HashMap.class, retType = {})
    // 0 is item, 2 is affix instance
    // Map.put(DynamicHolder<? extends Affix>, AffixInstance)
    // java/util/HashMap.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    public static AffixInstance hookAffixApply(IInserterContext<HashMap<DynamicHolder<? extends Affix>, AffixInstance>, INull> ctx, Object... args) {
        AffixApplyEvent event = new AffixApplyEvent((ItemStack) args[0], (AffixInstance) args[2]);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAffix();
    }
}
