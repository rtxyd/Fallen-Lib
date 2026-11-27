package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public record Constructors3<I extends ICodecProvider<? super I>>(Supplier<? extends IVanillaLikeCustomPacketPayload> beginConstructor, BiFunction<ResourceLocation, I, ? extends IVanillaLikeCustomPacketPayload> processConstructor, Supplier<? extends IVanillaLikeCustomPacketPayload> endConstructor) {
}
