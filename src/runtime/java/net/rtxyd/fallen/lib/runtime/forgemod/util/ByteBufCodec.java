package net.rtxyd.fallen.lib.runtime.forgemod.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.rtxyd.fallen.lib.runtime.forgemod.network.ClientBoundSyncExtraGemBonusesPacket;
import org.jetbrains.annotations.NotNull;

public interface ByteBufCodec<B, C> {

    void encode(@NotNull C value, @NotNull B buf);

    @NotNull C decode(@NotNull B buf);
}
