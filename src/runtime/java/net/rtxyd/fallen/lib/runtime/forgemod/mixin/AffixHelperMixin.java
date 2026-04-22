package net.rtxyd.fallen.lib.runtime.forgemod.mixin;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.event.AffixCacheRefreshEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis.event.AffixGetFinishEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

@Mixin(value = AffixHelper.class, remap = false)
public class AffixHelperMixin {

    @ModifyArg(method = "getAffixes(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;",
            at = @At(value = "INVOKE",
                    target = "Ldev/shadowsoffire/placebo/util/CachedObject$CachedObjectSource;getOrCreate(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Ljava/util/function/Function;Ljava/util/function/ToIntFunction;)Ljava/lang/Object;"),
            index = 2)
    private static Function<ItemStack, Map<DynamicHolder<? extends Affix>, AffixInstance>> hookCacheUpdate(Function<ItemStack, Map<DynamicHolder<? extends Affix>, AffixInstance>> deserializer) {
        return stack -> {
            Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = deserializer.apply(stack);
            AffixCacheRefreshEvent event = new AffixCacheRefreshEvent(affixes, stack);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getAffixes();
        };
    }

    @Inject(method = "getAffixes(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;", at = @At(value = "RETURN"), cancellable = true)
    private static void hookGetAffixes(ItemStack stack, CallbackInfoReturnable<Map<DynamicHolder<? extends Affix>, AffixInstance>> cir) {
        if (cir.getReturnValue().equals(Collections.emptyMap())) return;
        AffixGetFinishEvent event = new AffixGetFinishEvent(stack, cir.getReturnValue());
        MinecraftForge.EVENT_BUS.post(event);
        cir.setReturnValue(event.getAffixesView());
    }
}