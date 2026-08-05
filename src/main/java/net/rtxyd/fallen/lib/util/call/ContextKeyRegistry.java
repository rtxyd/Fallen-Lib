package net.rtxyd.fallen.lib.util.call;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ContextKeyRegistry {

    private final Map<String, ContextKey<?>> REGISTRY = new HashMap<>();

    public <T extends ContextKey<?>> T register(String id, Function<String, T> function) {
        validate(id);
        if (REGISTRY.containsKey(id)) {
            throw new IllegalStateException("Duplicated ContextKey: " + id);
        }
        T key = ContextKey.create(id, function);
        REGISTRY.put(id, key);
        return key;
    }

    @SuppressWarnings("unchecked")
    public <T extends ContextKey<?>> T get(String id) {
        return (T) REGISTRY.get(id);
    }

    public static void validate(String id) {
        String[] parts = id.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid ContextKey: " + id);
        }
        for (String p : parts) {
            if (p.isEmpty()) {
                throw new IllegalArgumentException("Empty segment: " + id);
            }
        }
    }

    public void forEachContextKey(Consumer<ContextKey<?>> consumer) {
        REGISTRY.values().forEach(consumer);
    }
}