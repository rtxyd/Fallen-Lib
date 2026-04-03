package net.rtxyd.fallen.lib.runtime.forgemod.mixin;

import dev.shadowsoffire.apotheosis.adventure.AdventureConfig;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LootCategory.class, remap = false)
public class LootCategoryFix {
    @Unique
    private static final ThreadLocal<ItemStack> FGA$lastItem = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
    @Unique
    private static final ThreadLocal<Boolean> FGA$recurseMark = ThreadLocal.withInitial(() -> false);
    @Inject(method = "forItem", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    private static void forItemGuardA(ItemStack item, CallbackInfoReturnable<LootCategory> cir) {
        if (FGA$recurseMark.get() && FGA$lastItem.get() == item) {
            LootCategory cat = AdventureConfig.TYPE_OVERRIDES.putIfAbsent(ForgeRegistries.ITEMS.getKey(item.getItem()), LootCategory.NONE);
            if (cat == null) {
                FallenLib.LOGGER.error("Detected recursion when invoke forItem() for {}, fallback LootCategory.NONE.", item.getItem());
            }
            cir.setReturnValue(LootCategory.NONE);
        }
        FGA$lastItem.set(item);
        FGA$recurseMark.set(true);
    }

    @Inject(method = "forItem", at = @At("RETURN"))
    private static void forItemGuardB(ItemStack item, CallbackInfoReturnable<LootCategory> cir) {
        FGA$lastItem.set(ItemStack.EMPTY);
        FGA$recurseMark.set(false);
    }
}
