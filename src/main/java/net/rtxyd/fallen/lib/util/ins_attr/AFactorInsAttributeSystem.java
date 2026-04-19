package net.rtxyd.fallen.lib.util.ins_attr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AFactorInsAttributeSystem<CLASSIFIER, INSTANCE, ATTRIBUTE extends AInsAttribute<INSTANCE>, FACTOR> implements IInsAttributeSystem<CLASSIFIER, INSTANCE, ATTRIBUTE> {

    private final Map<CLASSIFIER, INSTANCE> instances;
    private final FACTOR factor;
    private final Map<CLASSIFIER, ATTRIBUTE> attributes;

    public AFactorInsAttributeSystem(Map<CLASSIFIER, INSTANCE> instances, FACTOR factor) {
        this.instances = instances;
        this.factor = factor;
        this.attributes = Collections.unmodifiableMap(buildAttributes());
    }

    @Override
    public final Map<CLASSIFIER, ATTRIBUTE> getAttributes() {
        return attributes;
    }

    public final FACTOR getFactor() {
        return factor;
    }

    private Map<CLASSIFIER, ATTRIBUTE> buildAttributes() {
        Map<CLASSIFIER, ATTRIBUTE> attrs = new HashMap<>(instances.size());
        for (Map.Entry<CLASSIFIER, INSTANCE> en : instances.entrySet()) {
            attrs.put(en.getKey(), parse(en));
        }
        return attrs;
    }

    public abstract ATTRIBUTE parse(Map.Entry<CLASSIFIER, INSTANCE> en);

    public abstract INSTANCE createInsWith(INSTANCE old, float value);

    @Override
    public final Map<CLASSIFIER, INSTANCE> getInput() {
        return instances;
    }

    @Override
    public final Map<CLASSIFIER, INSTANCE> output() {
        Map<CLASSIFIER, INSTANCE> result = new HashMap<>();
        for (Map.Entry<CLASSIFIER, ATTRIBUTE> entry : attributes.entrySet()) {
            result.put(entry.getKey(), entry.getValue().output(this::createInsWith));
        }
        return result;
    }
}
