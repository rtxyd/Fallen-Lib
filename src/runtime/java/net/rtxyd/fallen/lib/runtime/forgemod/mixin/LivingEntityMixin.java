package net.rtxyd.fallen.lib.runtime.forgemod.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.mob_effect.EffectsTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingEntityMixin implements LivingEntityAccessor {

    @Inject(method = "tickEffects", at = @At("HEAD"))
    private void onTickEffects(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.level().isClientSide) {
            NeoForge.EVENT_BUS.post(new EffectsTickEvent.Pre(entity));
        }
    }
}
