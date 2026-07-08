package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class BoundHolder<T extends ICodecProvider<T>> implements IHolder<ResourceLocation, T> {

    public static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("null", "null");
    private final ResourceLocation id;
    private final IHolderOwner<ResourceLocation, T> owner;

    private T value;

    public BoundHolder(ResourceLocation id, IHolderOwner<ResourceLocation, T> owner) {
        this.id = id;
        this.owner = owner;
    }

    public void bind() {
        if (value == null) {
            value = owner.fallen_lib$getValue(id);
        }
    }

    public boolean isValid() {
        bind();
        return value != null;
    }

    @Override
    public ResourceLocation fallen_lib$getClassifier() {
        return getId();
    }

    @Override
    public T get() {
        bind();
        return Objects.requireNonNull(value, "No value provided: " + id);
    }

    public void reset() {
        value = null;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof BoundHolder<?> h && owner == h.owner && id.equals(h.id);
    }

    @Override
    public int hashCode() {
        return 31 * System.identityHashCode(owner) + id.hashCode();
    }
}
