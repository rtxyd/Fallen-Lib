package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IRegistryProcessPacketFunction<I extends ICodecProvider<I>, P extends IVanillaLikeCustomPacketPayload> extends BiFunction<ResourceLocation, Supplier<I>, P> {
}
