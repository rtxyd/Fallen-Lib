package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;

import java.util.function.Supplier;

public class ExtraGemBonusPayload extends LazyRegistryBoundPacketPayLoad<ExtraGemBonusRegistry.ExtraGemBonus> {
    public static final String version = "1.0";
    public static final IVanillaLikeCustomPacketPayload.Type<ClientBoundSyncExtraGemBonusesPacket> TYPE = IVanillaLikeCustomPacketPayload.createType(FallenLib.MODID, "egb_cl");
    public static final FriendlyByteBufCodec<LazyRegistryBoundPacketPayLoad<ExtraGemBonusRegistry.ExtraGemBonus>> BUF_CODEC =
            createLazyByteBufCodec(FallenLib.LOGGER, ExtraGemBonusRegistry.ExtraGemBonus.CODEC, ExtraGemBonusPayload::new);

    public ExtraGemBonusPayload(ResourceLocation path, Supplier<ExtraGemBonusRegistry.ExtraGemBonus> item) {
        super(path, item);
    }

    public static class Begin implements LazyRegistryBoundPacketPayLoad.IBegin {

        @Override
        public Class<ExtraGemBonusPayload> getProcessClass() {
            return ExtraGemBonusPayload.class;
        }

    }

    public static class End implements LazyRegistryBoundPacketPayLoad.IEnd {

        @Override
        public Class<ExtraGemBonusPayload> getProcessClass() {
            return ExtraGemBonusPayload.class;
        }
    }
}
