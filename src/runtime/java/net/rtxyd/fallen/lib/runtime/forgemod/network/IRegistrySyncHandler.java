package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface IRegistrySyncHandler<E> {
    void begin(@Nullable ServerPlayer player);
    void process(@Nullable ServerPlayer player, ResourceLocation path, E item);
    void end(@Nullable ServerPlayer player);
}