package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemAttributeModifierFactory {
    private final Map<Holder<Attribute>, OperationValues<UUID>> ATTR_UUID_CACHE = new HashMap<>();
    private final Map<Holder<Attribute>, OperationValues<String>> ATTR_NAME_CACHE = new HashMap<>();
    private final String prefixName;
    public ItemAttributeModifierFactory(String prefixName) {
        this.prefixName = prefixName;
    }
    public record ItemModifierValues(Holder<Attribute> attr, double addition, double multipliedBase, double multipliedTotal) {}
    public record OperationValues<T>(T addition, T multipliedBase, T multipliedTotal) {}

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
    public OperationValues<UUID> getGlobalUUIDs(Holder<Attribute> attr) {
        return ATTR_UUID_CACHE.computeIfAbsent(attr, v -> {
            OperationValues<String> names = getAttrNames(attr);
            return new OperationValues<>(
                    UUID.nameUUIDFromBytes(names.addition.getBytes(StandardCharsets.UTF_8)),
                    UUID.nameUUIDFromBytes(names.multipliedBase.getBytes(StandardCharsets.UTF_8)),
                    UUID.nameUUIDFromBytes(names.multipliedTotal.getBytes(StandardCharsets.UTF_8))
            );
        });
    }
    public OperationValues<String> getAttrNames(Holder<Attribute> attr) {
        return ATTR_NAME_CACHE.computeIfAbsent(attr, a -> new OperationValues<>(
                prefixName + "[" + attr.getKey() + 0 + "]",
                prefixName + "[" + attr.getKey() + 1 + "]",
                prefixName + "[" + attr.getKey() + 2 + "]")
        );
    }
}
