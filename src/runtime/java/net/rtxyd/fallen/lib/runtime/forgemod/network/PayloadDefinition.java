package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record PayloadDefinition<T extends IVanillaLikeCustomPacketPayload> (
        CustomPacketPayload.Type<T> type,
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
        IVanillaLikePayloadHandler<T> handler
) {
}