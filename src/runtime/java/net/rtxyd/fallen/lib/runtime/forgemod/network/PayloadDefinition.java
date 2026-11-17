package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;

public record PayloadDefinition<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        FriendlyByteBufCodec<T> codec,
        IPayloadHandler<T> handler
) {}