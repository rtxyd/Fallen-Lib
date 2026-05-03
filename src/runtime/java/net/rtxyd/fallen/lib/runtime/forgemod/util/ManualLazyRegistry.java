package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ManualLazyRegistry<H, T> {
    private final Map<ResourceLocation, ManualLazy<H, T>> lazies = new HashMap<>();
    public final Map<ResourceLocation, ManualLazy<H, T>> laziesView = Collections.unmodifiableMap(lazies);
    public ManualLazy<H,T> register(ResourceLocation loc, ManualLazy<H,T> item) {
        lazies.put(loc, item);
        return item;
    }
    public Map<ResourceLocation, ManualLazy<H, T>> getLaziesView() {
        return laziesView;
    }
}
