package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface IVanillaLikeCustomPacketPayload {
    @NotNull IVanillaLikeCustomPacketPayload.Type<? extends IVanillaLikeCustomPacketPayload> type();

    public class Type<T> {
        private final ResourceLocation ID;
        public Type(ResourceLocation id) {
            this.ID = id;
        }
    }
}
