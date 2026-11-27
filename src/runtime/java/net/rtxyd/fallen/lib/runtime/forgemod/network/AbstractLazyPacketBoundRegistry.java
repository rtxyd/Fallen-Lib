/*
package net.rtxyd.fallen.lib.runtime.forgemod.network;

import net.minecraft.resources.ResourceLocation;
import net.rtxyd.fallen.lib.runtime.forgemod.util.ICodecProvider;
import net.rtxyd.fallen.lib.runtime.forgemod.util.IHolderOwner;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractLazyPacketBoundRegistry<E extends ICodecProvider<? super E>> extends AbstractPacketBoundRegistry<E> implements ICodecProvider<E>, IHolderOwner<ResourceLocation, E> {

    protected AbstractLazyPacketBoundRegistry(Logger logger, String path, String type, Predicate<ResourceLocation> locFilter, boolean doSync, boolean useTypeIdAsKey) {
        super(logger, path, type, locFilter, doSync, useTypeIdAsKey);
    }

    @Nullable
    public E fallen_lib$getValue(ResourceLocation key) {
        return getValue(key);
    }
}
*/
