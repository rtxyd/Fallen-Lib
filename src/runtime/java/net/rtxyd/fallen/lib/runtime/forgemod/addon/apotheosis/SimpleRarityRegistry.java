package net.rtxyd.fallen.lib.runtime.forgemod.addon.apotheosis;

import java.util.*;

public class SimpleRarityRegistry<C, T> {
    protected final Map<C, AFallenRarity<C, T>> rarityMap = new LinkedHashMap<>();
    protected Map<C, AFallenRarity<C, T>> rarityMapView = Collections.unmodifiableMap(rarityMap);

    public void register(AFallenRarity<C, T> item) {
        int id = rarityMap.size();
        item.acceptIndex(id);
        if (rarityMap.put(item.getClassifier(), item) != null) {
            throw new UnsupportedOperationException("Duplicated entry! Id: " + item.getClassifier());
        }
    }

    public void reset() {
        rarityMap.clear();
    }

    public Map<C, AFallenRarity<C, T>> getRarityMapView() {
        return rarityMapView;
    }
}
