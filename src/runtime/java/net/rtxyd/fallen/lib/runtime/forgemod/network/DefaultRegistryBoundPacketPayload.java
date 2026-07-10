package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.GameLifecycleHelper;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.runtime.forgemod.util.MiscUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DefaultRegistryBoundPacketPayload<E extends ICodecProvider<E>> extends AbstractRegistryBoundPacketPayload<E> {

    public static final String version = "1.0";
    public static final Type<ClientBoundSyncExtraGemBonusesPacket> TYPE = IVanillaLikeCustomPacketPayload.createType(FallenLib.MODID, "default_cl");
    private final String regPath;
    public static final DefaultRegistryBoundPacketPayload<?> EMPTY = new DefaultRegistryBoundPacketPayload<>(null, null, "");

    public String getRegPath() {
        return regPath;
    }

    @Override
    public @NotNull Type<ClientBoundSyncExtraGemBonusesPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (getPath() == null || getItem() == null) return;
        DefaultPacketBoundRegistry<E> registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(regPath);
        if (registry == null) {
            DefaultPacketBoundRegistry.LOGGER.error(String.format("Registry singleton for loading [%s] is not registered! Skipped.", getPath()));
            return;
        }
        registry.handleProcess(contextSupplier, getPath(), getItem());
    }

    public DefaultRegistryBoundPacketPayload(ResourceLocation path, E o, String regPath) {
        super(path, o);
        this.regPath = regPath;
    }

    public static class Begin implements IBegin {
        public final Type<Begin> TYPE = IVanillaLikeCustomPacketPayload.createType(FallenLib.MODID, "default_begin");
        private final String path;
        public static final FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload.Begin> DEFAULT_BUF_CODEC = MiscUtil.createSingleStringBufCodec(Begin::getRegPath, Begin::new);

        private String getRegPath() {
            return path;
        }

        public Begin(String path) {
            this.path = path;
        }

        @Override
        public Class<?> getProcessClass() {
            return DefaultRegistryBoundPacketPayload.class;
        }

        @Override
        public @NotNull Type<Begin> type() {
            return TYPE;
        }

        @Override
        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            DefaultPacketBoundRegistry<?> registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(path);
            if (registry == null) return;
            registry.handleBegin(contextSupplier);
        }
    }

    public static class End implements IEnd {
        public final Type<End> TYPE = IVanillaLikeCustomPacketPayload.createType(FallenLib.MODID, "default_end");
        private final String path;

        private String getRegPath() {
            return path;
        }

        public static final FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload.End> DEFAULT_BUF_CODEC = MiscUtil.createSingleStringBufCodec(End::getRegPath, End::new);

        public End(String path) {
            this.path = path;
        }

        @Override
        public Class<?> getProcessClass() {
            return DefaultRegistryBoundPacketPayload.class;
        }

        @Override
        public @NotNull Type<End> type() {
            return TYPE;
        }

        @Override
        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            var registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(path);
            if (registry == null) return;
            registry.handleEnd(contextSupplier);
        }
    }
}
