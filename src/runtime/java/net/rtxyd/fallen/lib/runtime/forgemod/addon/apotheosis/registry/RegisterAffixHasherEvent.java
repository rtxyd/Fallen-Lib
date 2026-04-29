package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.registry;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.util.IObjectCaky;

/**
 * Not used
 */
public class RegisterAffixHasherEvent extends Event implements IModBusEvent {
    public void register(IObjectCaky.CakyReviewer<ItemStack> reviewer) {
        if (FallenLib.getStage() != FallenLib.Stage.LOADING) return;
        AffixHashRegistry.register(reviewer);
    }
}
