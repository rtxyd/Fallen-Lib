package net.rtxyd.fallen.lib.runtime.forgemod.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ServerPlayer.class, remap = false)
public interface ServerPlayerAccessor {

    @Accessor("levitationStartPos")
    void setLevitationStartPos(Vec3 levitationStartPos);
}
