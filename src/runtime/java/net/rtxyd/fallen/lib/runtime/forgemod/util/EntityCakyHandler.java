package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.world.entity.Entity;
import net.rtxyd.fallen.lib.util.IObjectCaky;

public interface EntityCakyHandler {
    public static <T> T resolveWith(Entity entity, String key, IObjectCaky.Type type, IObjectCaky.CakyLoader<Entity, T> loader, IObjectCaky.CakyReviewer<Entity> reviewer) {
        return ((EntityCakyHandler) entity).fallen_lib$getObjectCakyWith(key, type, loader, reviewer);
    }

    <T> T fallen_lib$getObjectCakyWith(String key, IObjectCaky.Type type, IObjectCaky.CakyLoader<Entity, T> loader, IObjectCaky.CakyReviewer<Entity> reviewer);
}
