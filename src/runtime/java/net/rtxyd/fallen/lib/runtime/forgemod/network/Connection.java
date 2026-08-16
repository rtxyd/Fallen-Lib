package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.rtxyd.fallen.lib.runtime.forgemod.util.FriendlyByteBufCodec;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;

import java.util.*;
import java.util.function.*;

@EventBusSubscriber
public class Connection {
    private static final Set<IPayloadRegisterHelper> REGISTERED = new LinkedHashSet<>();
    public static final String VERSION = "1.0";

    public static void register(IPayloadRegisterHelper helper) {
        REGISTERED.add(helper);
    }

    @SubscribeEvent
    static void registerAll(RegisterPayloadHandlersEvent event) {
        registerInternal();
        List<IPayloadRegisterHelper> sorted = REGISTERED.stream().sorted(Comparator.comparing(IPayloadRegisterHelper::getSortPriority).reversed()).toList();

        for (IPayloadRegisterHelper helper : sorted) {
            helper.register();
            helper.initSingleton();
        }
    }

    private static void registerInternal() {
        NetworkRegistry.register(
                DefaultRegistryBoundPacketPayload.Begin.TYPE,
                DefaultRegistryBoundPacketPayload.Begin.DEFAULT_BUF_CODEC,
                DefaultRegistryBoundPacketPayload.Begin::handle,
                List.of(ConnectionProtocol.PLAY),
                Optional.of(PacketFlow.CLIENTBOUND),
                VERSION,
                false
        );
        NetworkRegistry.register(
                DefaultRegistryBoundPacketPayload.TYPE,
                (FriendlyByteBufCodec<DefaultRegistryBoundPacketPayload>)(Object) DefaultRegistryBoundPacketPayload.STREAM_CODEC,
                DefaultRegistryBoundPacketPayload::handle,
                List.of(ConnectionProtocol.PLAY),
                Optional.of(PacketFlow.CLIENTBOUND),
                VERSION,
                false
        );
        NetworkRegistry.register(
                DefaultRegistryBoundPacketPayload.End.TYPE,
                DefaultRegistryBoundPacketPayload.End.DEFAULT_BUF_CODEC,
                DefaultRegistryBoundPacketPayload.End::handle,
                List.of(ConnectionProtocol.PLAY),
                Optional.of(PacketFlow.CLIENTBOUND),
                VERSION,
                false
        );
        DefaultPacketBoundRegistry.registerSyncDefault();
    }

    public static void init(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
        });
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, MSG data) {
        player.connection.send(data);
    }

    public static <MSG extends CustomPacketPayload> void sendToAllPlayers(MSG data) {
        PacketDistributor.sendToAllPlayers(data);
    }
}
