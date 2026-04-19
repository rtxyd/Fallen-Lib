package net.rtxyd.fallen.lib.util.ins_attr;

public abstract class AInsAttributeModifier {
    private final Type type;
    private final float value;

    protected AInsAttributeModifier(Type type, float value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }
    public float getValue() {
        return value;
    }

    public static enum Type {
        MULTIPLY_BASE,
        ADD_BASE,
        SET_BASE,
        MULTIPLY_FINAL,
        ADD_FINAL,
        SET_FINAL
    }
}
