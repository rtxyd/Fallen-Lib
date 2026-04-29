package net.rtxyd.fallen.lib.runtime.forgemod.mixin;

import net.minecraft.world.item.ItemStack;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ItemStackCakyHandler;
import net.rtxyd.fallen.lib.util.FinalObjectCaky;
import net.rtxyd.fallen.lib.util.IObjectCaky;
import net.rtxyd.fallen.lib.util.ManualObjectCaky;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackCakyHandler {
    @Unique
    private volatile IObjectCaky fallen_lib$caky = null;

    @Unique
    @Override
    public <T> T fallen_lib$getObjectCaky(String id, IObjectCaky.CakyLoader<ItemStack, T> loader, IObjectCaky.CakyReviewer<ItemStack> reviewer) {
        if (fallen_lib$caky == null) {
            synchronized (this) {
                fallen_lib$caky = new FinalObjectCaky();
            }
        }
        return fallen_lib$caky.resolve((ItemStack) (Object) this, id, loader, reviewer);
    }

    @Override
    public <T> T fallen_lib$getObjectCakyWith(String id, IObjectCaky.Type type, IObjectCaky.CakyLoader<ItemStack, T> loader, IObjectCaky.CakyReviewer<ItemStack> reviewer) {
        if (fallen_lib$caky == null) {
            synchronized (this) {
                switch (type) {
                    case FINAL -> {
                        fallen_lib$caky = new FinalObjectCaky();
                    }
                    case MANUAL -> {
                        fallen_lib$caky = new ManualObjectCaky();
                    }
                }
            }
        }
        return fallen_lib$caky.resolve((ItemStack) (Object) this, id, loader, reviewer);
    }
}
