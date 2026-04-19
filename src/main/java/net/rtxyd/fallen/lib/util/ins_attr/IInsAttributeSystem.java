package net.rtxyd.fallen.lib.util.ins_attr;

import java.util.Map;

public interface IInsAttributeSystem<CLASSIFIER, INSTANCE, ATTRIBUTE extends AInsAttribute<INSTANCE>> {

    Map<CLASSIFIER, ATTRIBUTE> getAttributes();

    Map<CLASSIFIER, INSTANCE> getInput();

    Map<CLASSIFIER, INSTANCE> output();
}