package net.rtxyd.fallen.lib.runtime.forgemod.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.rtxyd.fallen.lib.runtime.forgemod.util.EntityCakyHandler;
import net.rtxyd.fallen.lib.util.FinalObjectCaky;
import net.rtxyd.fallen.lib.util.IObjectCaky;
import net.rtxyd.fallen.lib.util.ManualObjectCaky;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EntityCakyHandler {
    @Unique
    private volatile IObjectCaky fallen_lib$caky = null;

    @Override
    public <T> T fallen_lib$getObjectCakyWith(String id, IObjectCaky.Type type, IObjectCaky.CakyLoader<Entity, T> loader, IObjectCaky.CakyReviewer<Entity> reviewer) {
        if (fallen_lib$caky == null) {
            synchronized (this) {
                switch (type) {
                    case FINAL -> {
                        fallen_lib$caky = new FinalObjectCaky();
                    }
                    case MANUAL -> {
                        fallen_lib$caky = new ManualObjectCaky();
                    }
                }
            }
        }
        return fallen_lib$caky.resolve((Entity) (Object) this, id, loader, reviewer);
    }
}
