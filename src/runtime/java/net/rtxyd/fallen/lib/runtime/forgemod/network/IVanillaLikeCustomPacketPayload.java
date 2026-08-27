package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public interface IVanillaLikeCustomPacketPayload extends CustomPacketPayload {
    CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type();

    void handle(IPayloadContext contextSupplier);

    static <T extends CustomPacketPayload> CustomPacketPayload.@NotNull Type<T> createType(String namespace, String path) {
        return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
