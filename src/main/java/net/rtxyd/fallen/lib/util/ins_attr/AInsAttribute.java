package net.rtxyd.fallen.lib.util.ins_attr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class AInsAttribute<INSTANCE> {
    private final INSTANCE instance;
    private Map<String, AInsAttributeModifier> modifiers;
    protected float initBase;
    protected float initFinal;
    private float multiplyBase;
    private float addBase;
    private float setBase;
    private float multiplyFinal;
    private float addFinal;
    private float setFinal;

    public AInsAttribute(INSTANCE instance, Map<String, AInsAttributeModifier> modifiers, float initBase, float initFinal) {
        this.instance = instance;
        this.modifiers = modifiers;
        this.initBase = initBase;
        this.initFinal = initFinal;
        this.multiplyBase = 1.0f;
        this.addBase = 0;
        this.setBase = initBase;
        this.multiplyFinal = 1.0f;
        this.addFinal = 0;
        this.setFinal = initFinal;
    }

    public void reset() {
        this.multiplyBase = 1.0f;
        this.addBase = 0;
        this.setBase = initBase;
        this.multiplyFinal = 1.0f;
        this.addFinal = 0;
        this.setFinal = initFinal;
        clearModifier();
    }

    public final void addModifier(String name, AInsAttributeModifier modifier) {
        modifiers.put(name, modifier);
    }

    public final void removeModifier(String name) {modifiers.remove(name);}

    protected final void clearModifier() {
        modifiers = new HashMap<>();
    }

    protected final void calc(AInsAttributeModifier modifier) {
        float value = modifier.getValue();
        switch (modifier.getType()) {
            case MULTIPLY_BASE -> {
                this.calcMultiplyBase(value);
            }
            case ADD_BASE -> {
                this.calcAddBase(value);
            }
            case SET_BASE -> {
                this.calcSetBase(value);
            }
            case MULTIPLY_FINAL -> {
                this.calcMultiplyFinal(value);
            }
            case ADD_FINAL -> {
                this.calcAddFinal(value);
            }
            case SET_FINAL -> {
                this.calcSetFinal(value);
            }
        }
    }

    public INSTANCE output(BiFunction<INSTANCE, Float, INSTANCE> function) {
        modifiers.values().forEach(this::calc);
        computeFinal();
        return function.apply(instance, setFinal);
    }

    protected final void computeFinal() {
        calcSetBase(initBase * multiplyBase + addBase);
        calcSetFinal(setBase * multiplyFinal + addFinal);
    }

    protected final void calcMultiplyBase(float value) {
        this.multiplyBase += value;
    }

    protected final void calcAddBase(float value) {
        this.addBase += value;
    }

    protected final void calcSetBase(float value) {
        if (this.setBase < value) {
            this.setBase = value;
        }
    }

    protected final void calcMultiplyFinal(float value) {
        this.multiplyFinal += value;
    }

    protected final void calcAddFinal(float value) {
        this.addFinal += value;
    }

    protected final void calcSetFinal(float value) {
        if (this.setFinal < value) {
            this.setFinal = value;
        }
    }
}