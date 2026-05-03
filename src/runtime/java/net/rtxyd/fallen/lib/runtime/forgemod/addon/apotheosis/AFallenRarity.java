package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AFallenRarity<C, T> implements Comparable<AFallenRarity<C, T>> {
    protected final C classifier;
    protected final @Nullable T rarity;
    protected int index;

    protected AFallenRarity(C classifier, @Nullable T rarity) {
        this.classifier = classifier;
        this.rarity = rarity;
    }

    public @Nullable T getRarity() {
        return rarity;
    }

    public C getClassifier() {
        return classifier;
    }

    public int getIndex() {
        return index;
    }

    protected final void acceptIndex(int index) {
        this.index = index;
    }

    @Override
    public final int compareTo(@NotNull AFallenRarity<C, T> o) {
        return Integer.compare(index, o.index);
    }
}
