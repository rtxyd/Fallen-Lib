package net.rtxyd.fallen.lib.runtime.forgemod.util;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemAttributeModifierFactory {
    private final Map<Attribute, OperationValues<UUID>> ATTR_UUID_CACHE = new HashMap<>();
    private final Map<Attribute, OperationValues<String>> ATTR_NAME_CACHE = new HashMap<>();
    private final String prefixName;
    public ItemAttributeModifierFactory(String prefixName) {
        this.prefixName = prefixName;
    }
    public record ItemModifierValues(Attribute attr, double addition, double multipliedBase, double multipliedTotal) {}
    public record OperationValues<T>(T addition, T multipliedBase, T multipliedTotal) {}

    public List<ItemModifierValues> computeModifierValues(ItemAttributeModifierEvent event) {
        var multiModifiers = event.getModifiers();
        List<ItemModifierValues> list = new ArrayList<>();
        for (Map.Entry<Attribute, Collection<AttributeModifier>> en : multiModifiers.asMap().entrySet()) {
            double addition = 0;
            double multipliedBase = 0;
            double multipliedTotal = 0;
            Collection<AttributeModifier> v = en.getValue();
            for (AttributeModifier modifier : v) {
                switch (modifier.getOperation()) {
                    case ADDITION -> {
                        addition += modifier.getAmount();
                    }
                    case MULTIPLY_BASE -> {
                        multipliedBase += modifier.getAmount();
                    }
                    case MULTIPLY_TOTAL -> {
                        multipliedTotal += modifier.getAmount();
                    }
                }
            }
            list.add(new ItemModifierValues(en.getKey(), addition, multipliedBase, multipliedTotal));
        }
        return list;
    }
    public OperationValues<UUID> getGlobalUUIDs(Attribute attr) {
        return ATTR_UUID_CACHE.computeIfAbsent(attr, v -> {
            OperationValues<String> names = getAttrNames(attr);
            return new OperationValues<>(
                    UUID.nameUUIDFromBytes(names.addition.getBytes(StandardCharsets.UTF_8)),
                    UUID.nameUUIDFromBytes(names.multipliedBase.getBytes(StandardCharsets.UTF_8)),
                    UUID.nameUUIDFromBytes(names.multipliedTotal.getBytes(StandardCharsets.UTF_8))
            );
        });
    }
    public OperationValues<String> getAttrNames(Attribute attr) {
        return ATTR_NAME_CACHE.computeIfAbsent(attr, a -> new OperationValues<>(
                prefixName + "[" + ForgeRegistries.ATTRIBUTES.getKey(attr) + 0 + "]",
                prefixName + "[" + ForgeRegistries.ATTRIBUTES.getKey(attr) + 1 + "]",
                prefixName + "[" + ForgeRegistries.ATTRIBUTES.getKey(attr) + 2 + "]")
        );
    }
}
