package net.rtxyd.fallen.lib.runtime.forgemod.mixin.client;

import dev.shadowsoffire.attributeslib.client.AttributesLibClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AttributesLibClient.class)
@OnlyIn(Dist.CLIENT)
public class AttributesLibTooltipFix {
    @Unique
    private static boolean fallen_lib$shouldNeg = false;
    @ModifyArg(method = "applyTextFor",
            at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/TextColor;fromRgb(I)Lnet/minecraft/network/chat/TextColor;"
    ))
    private static int checkColor(int pColor){
        if (pColor == 0xF93131) {
            fallen_lib$shouldNeg = true;
        }
        return pColor;
    }

    @ModifyArg(
            method = "applyTextFor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;<init>(Ljava/util/UUID;Ljava/util/function/Supplier;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)V"
            ),
            index = 2
    )
    private static double fixNeg(double value) {
        if (fallen_lib$shouldNeg) {
            return value < 0 ? value : -value;
        }
        return value;
    }
}