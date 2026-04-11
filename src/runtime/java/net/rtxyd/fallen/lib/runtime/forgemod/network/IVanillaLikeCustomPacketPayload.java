package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IVanillaLikeCustomPacketPayload {
    @NotNull IVanillaLikeCustomPacketPayload.Type<? extends IVanillaLikeCustomPacketPayload> type();

    void handle(Supplier<NetworkEvent.Context> contextSupplier);

    static <T> Type<T> createType(String namespace, String path) {
        return new Type<>(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public class Type<T> {
        private final ResourceLocation ID;
        public Type(ResourceLocation id) {
            this.ID = id;
        }

        public ResourceLocation getId() {
            return ID;
        }
    }
}
