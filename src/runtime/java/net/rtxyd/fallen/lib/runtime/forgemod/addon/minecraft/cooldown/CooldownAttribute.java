package net.rtxyd.fallen.lib.runtime.forgemod.addon.minecraft.cooldown;

import net.rtxyd.fallen.lib.util.ins_attr.AInsAttribute;
import net.rtxyd.fallen.lib.util.ins_attr.InsAttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class CooldownAttribute extends AInsAttribute<Long> {

    private final String id;
    protected final int cooldown;

    private CooldownAttribute(@NotNull String id, long startTime, int cooldown, Map<String, InsAttributeModifier> modifiers, float initBase, float initFinal) {
        super(startTime, modifiers, initBase, initFinal);
        this.id = id;
        this.cooldown = cooldown;
    }

    public boolean is(String id) {
        return this.id.endsWith(id);
    }

    public String getId() {
        return id;
    }

    public static CooldownAttribute of(String id, long startTime, int cooldown) {
        return new Modifiable(id, startTime, cooldown, new HashMap<>(), cooldown, cooldown);
    }

    public static CooldownAttribute ofUnmodifiable(String id, long startTime, int cooldown) {
        return new Unmodifiable(id, startTime, cooldown, Map.of(), -1, -1);
    }

    public long getStartTime() {
        return getInstance();
    }

    public abstract long outputAlt();

    /**
     * use outputAlt
     */
    @Deprecated
    @Override
    public Long output(BiFunction<Long, Float, Long> function) {
        return super.output(function);
    }

    public static class Unmodifiable extends CooldownAttribute {

        private Unmodifiable(@NotNull String id, long startTime, int cooldown, Map<String, InsAttributeModifier> modifiers, float initBase, float initFinal) {
            super(id, startTime, cooldown, modifiers, initBase, initFinal);
        }

        @Override
        public long outputAlt() {
            return getStartTime() + cooldown;
        }

        @Override
        public Long output(BiFunction<Long, Float, Long> function) {
            return outputAlt();
        }
    }

    public static class Modifiable extends CooldownAttribute {

        private Modifiable(@NotNull String id, long startTime, int cooldown, Map<String, InsAttributeModifier> modifiers, float initBase, float initFinal) {
            super(id, startTime, cooldown, modifiers, initBase, initFinal);
        }

        @Override
        public long outputAlt() {
            return super.output((a, b) ->  a + b.longValue());
        }

        @Override
        public Long output(BiFunction<Long, Float, Long> function) {
            return outputAlt();
        }
    }
}