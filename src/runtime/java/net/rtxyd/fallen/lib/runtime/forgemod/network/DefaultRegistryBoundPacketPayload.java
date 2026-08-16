package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rtxyd.fallen.lib.runtime.forgemod.FallenLib;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.runtime.forgemod.util.MiscUtil;
import org.jetbrains.annotations.NotNull;

public class DefaultRegistryBoundPacketPayload<E extends ICodecProvider<E>> extends AbstractRegistryBoundPacketPayload<E> {

    public static final String version = "1.0";
    @SuppressWarnings("rawtypes")
    public static final Type<DefaultRegistryBoundPacketPayload> TYPE = IVanillaLikeCustomPacketPayload.createType(FallenLib.MODID, "default_cl");
    private final String regPath;
    public static final DefaultRegistryBoundPacketPayload<?> EMPTY = new DefaultRegistryBoundPacketPayload<>(null, null, "");
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload> STREAM_CODEC = new FriendlyByteBufCodec<>() {
        @Override
        public @NotNull DefaultRegistryBoundPacketPayload decode(@NotNull FriendlyByteBuf buf) {
            buf.markReaderIndex();
            int length = buf.readInt();
            String regPath = buf.readUtf(length);
            var registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(regPath);
            if (registry == null) return DefaultRegistryBoundPacketPayload.EMPTY;
            var codec = registry.getDefaultBufCodec();
            if (codec == null) return DefaultRegistryBoundPacketPayload.EMPTY;
            buf.resetReaderIndex();
            return codec.decode(buf);
        }

        @Override
        public void encode(@NotNull FriendlyByteBuf buf, @NotNull DefaultRegistryBoundPacketPayload payload) {
            String regPath = payload.getRegPath();
            var registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(regPath);
            if (registry == null) return;
            FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload> codec = (FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload>) (Object) registry.getDefaultBufCodec();
            if (codec == null) return;
            codec.encode(buf, payload);
        }
    };

    public String getRegPath() {
        return regPath;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext contextSupplier) {
        if (getPath() == null || getItem() == null) return;
        DefaultPacketBoundRegistry<E> registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(regPath);
        if (registry == null) {
            FallenLib.LOGGER.error("Registry singleton for loading [{}] is not registered! Skipped.", getPath());
            return;
        }
        registry.handleProcess(contextSupplier, getPath(), getItem());
    }

    public DefaultRegistryBoundPacketPayload(ResourceLocation path, E o, String regPath) {
        super(path, o);
        this.regPath = regPath;
    }

    public static class Begin implements IBegin {
        public static final Type<Begin> TYPE = IVanillaLikeCustomPacketPayload.createType(FallenLib.MODID, "default_begin");
        private final String path;
        public static final FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload.Begin> DEFAULT_BUF_CODEC = MiscUtil.createSingleStringBufCodec(Begin::getRegPath, Begin::new);

        private String getRegPath() {
            return path;
        }

        public Begin(String path) {
            this.path = path;
        }

        @Override
        public Type<?> getProcessType() {
            return DefaultRegistryBoundPacketPayload.TYPE;
        }

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        @Override
        public void handle(IPayloadContext contextSupplier) {
            DefaultPacketBoundRegistry<?> registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(path);
            if (registry == null) return;
            registry.handleBegin(contextSupplier);
        }
    }

    public static class End implements IEnd {
        public static final Type<End> TYPE = IVanillaLikeCustomPacketPayload.createType(FallenLib.MODID, "default_end");
        private final String path;

        private String getRegPath() {
            return path;
        }

        public static final FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload.End> DEFAULT_BUF_CODEC = MiscUtil.createSingleStringBufCodec(End::getRegPath, End::new);

        public End(String path) {
            this.path = path;
        }

        @Override
        public Type<?> getProcessType() {
            return DefaultRegistryBoundPacketPayload.TYPE;
        }

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        @Override
        public void handle(IPayloadContext contextSupplier) {
            var registry = DefaultPacketBoundRegistry.getDefaultSingletonByPath(path);
            if (registry == null) return;
            registry.handleEnd(contextSupplier);
        }
    }
}
