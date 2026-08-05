package net.rtxyd.fallen.lib.util.call;

import java.util.function.Function;

/**
 * Format: abc.cde.efg
 * @param <T>
 */
public abstract class ContextKey<T> {

    private final String id;

    protected ContextKey(String id) {
        this.id = id;
    }

    static <C extends ContextKey<?>> C create(String id, Function<String, C> function) {
        ContextKeyRegistry.validate(id);
        return function.apply(id);
    }

    public String getId() {
        return id;
    }
}