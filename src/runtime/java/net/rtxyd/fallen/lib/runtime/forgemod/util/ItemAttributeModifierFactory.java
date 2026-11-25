package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.*;

public class ItemAttributeModifierFactory {
    private final Map<Holder<Attribute>, OperationValues<ResourceLocation>> ATTR_ID_CACHE = new HashMap<>();
    private final Map<Holder<Attribute>, OperationValues<String>> ATTR_NAME_CACHE = new HashMap<>();
    private final String nameSpace;
    private final String pathPrefix;
    public ItemAttributeModifierFactory(String nameSpace, String pathPrefix) {
        this.nameSpace = nameSpace;
        this.pathPrefix = pathPrefix;
    }
    public record ItemModifierValues(Holder<Attribute> attr, double addition, double multipliedBase, double multipliedTotal) {}
    public record OperationValues<T>(T addValue, T addMultipliedBase, T addMultipliedTotal) {}

    public List<ItemModifierValues> computeModifierValues(ItemAttributeModifierEvent event) {
        var multiModifiers = event.getModifiers();
        List<ItemModifierValues> list = new ArrayList<>();
        double addition = 0;
        double multipliedBase = 0;
        double multipliedTotal = 0;
        for (ItemAttributeModifiers.Entry en : multiModifiers) {
            AttributeModifier v = en.modifier();
            switch (v.operation()) {
                case ADD_VALUE -> {
                    addition += v.amount();
                }
                case ADD_MULTIPLIED_BASE -> {
                    multipliedBase += v.amount();
                }
                case ADD_MULTIPLIED_TOTAL -> {
                    multipliedTotal += v.amount();
                }
            }
            list.add(new ItemModifierValues(en.attribute(), addition, multipliedBase, multipliedTotal));
        }
        return list;
    }
    public OperationValues<ResourceLocation> getGlobalIDs(Holder<Attribute> attr) {
        return ATTR_ID_CACHE.computeIfAbsent(attr, v -> {
            OperationValues<String> names = getAttrNames(attr);
            return new OperationValues<>(
                    ResourceLocation.fromNamespaceAndPath(nameSpace, names.addValue),
                    ResourceLocation.fromNamespaceAndPath(nameSpace, names.addMultipliedBase),
                    ResourceLocation.fromNamespaceAndPath(nameSpace, names.addMultipliedTotal)
            );
        });
    }
    public OperationValues<String> getAttrNames(Holder<Attribute> attr) {
        return ATTR_NAME_CACHE.computeIfAbsent(attr, a -> new OperationValues<>(
                pathPrefix + "[" + attr.getKey() + 0 + "]",
                pathPrefix + "[" + attr.getKey() + 1 + "]",
                pathPrefix + "[" + attr.getKey() + 2 + "]")
        );
    }
}
