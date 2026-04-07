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
    private static final ThreadLocal<ItemStack> fallen_lib$lastItem = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
    @Unique
    private static final ThreadLocal<Boolean> fallen_lib$recurseMark = ThreadLocal.withInitial(() -> false);
    @Inject(method = "forItem", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    private static void forItemGuardA(ItemStack item, CallbackInfoReturnable<LootCategory> cir) {
        if (fallen_lib$recurseMark.get() && fallen_lib$lastItem.get() == item) {
            LootCategory cat = AdventureConfig.TYPE_OVERRIDES.putIfAbsent(ForgeRegistries.ITEMS.getKey(item.getItem()), LootCategory.NONE);
            if (cat == null) {
                FallenLib.LOGGER.error("Detected recursion when invoke forItem() for {}, fallback LootCategory.NONE.", item.getItem());
            }
            cir.setReturnValue(LootCategory.NONE);
        }
        fallen_lib$lastItem.set(item);
        fallen_lib$recurseMark.set(true);
    }

    @Inject(method = "forItem", at = @At("RETURN"))
    private static void forItemGuardB(ItemStack item, CallbackInfoReturnable<LootCategory> cir) {
        fallen_lib$lastItem.set(ItemStack.EMPTY);
        fallen_lib$recurseMark.set(false);
    }
}
